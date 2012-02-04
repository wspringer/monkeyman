package nl.flotsam.frontman.decorator.markdown

import nl.flotsam.frontman.Resource
import nl.flotsam.frontman.decorator.ResourceDecoration
import nl.flotsam.frontman.util.Closeables._
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine, Template}
import org.apache.commons.io.{FilenameUtils, IOUtils}
import java.io.{PrintWriter, StringWriter}
import org.pegdown.ast.{TextNode, HeaderNode}
import org.pegdown.{LinkRenderer, ToHtmlSerializer, PegDownProcessor}

case class MarkdownToHtmlDecoration(resource: Resource, template: Option[Template], engine: TemplateEngine)
  extends ResourceDecoration(resource) {

  lazy val (title, html) = {
    using(resource.open) {
      in =>
        val markdown = IOUtils.toString(in, "UTF-8")
        val processor = new PegDownProcessor
        val rootNode = processor.parseMarkdown(markdown.toCharArray)
        val visitor = new ToHtmlSerializer(new LinkRenderer) with TitleExtractor
        val html = visitor.toHtml(rootNode)
        val title = visitor.title
        (title, html)
    }
  }

  override def label = title.orElse(resource.label)

  override val contentType =
    if (template.isDefined) "text/html"
    else "text/plain"

  override lazy val path =
    if (template.isDefined) FilenameUtils.removeExtension(resource.path) + ".html"
    else resource.path

  override def open =
    if (template.isDefined) {
      val writer = new StringWriter
      val context = new DefaultRenderContext(path, engine, new PrintWriter(writer))
      context.attributes("body") = html
      context.attributes("title") = title
      template.get.render(context)
      IOUtils.toInputStream(writer.getBuffer, "UTF-8")
    } else {
      IOUtils.toInputStream(html)
    }

  trait TitleExtractor extends ToHtmlSerializer {
    var inheader = false
    var title: Option[String] = None

    override def visit(node: HeaderNode) {
      if (title.isEmpty) inheader = true
      super.visit(node)
      inheader = false
    }

    override def visit(node: TextNode) {
      if (inheader && title.isEmpty) title = Some(node.getText)
      super.visit(node)
    }
  }

}

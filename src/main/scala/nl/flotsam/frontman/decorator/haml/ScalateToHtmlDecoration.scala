package nl.flotsam.frontman.decorator.haml

import nl.flotsam.frontman.decorator.ResourceDecoration
import java.io.{PrintWriter, StringWriter}
import org.apache.commons.io.IOUtils
import nl.flotsam.frontman.Resource
import org.fusesource.scalate.{Template, DefaultRenderContext, TemplateEngine}
import org.apache.commons.io.FilenameUtils._

class ScalateToHtmlDecoration(resource: Resource, template: Template, engine: TemplateEngine)
  extends ResourceDecoration(resource)
{
  override val path = removeExtension(resource.path) + ".html"

  override def contentType = "text/html"

  override def open = {
    val writer = new StringWriter
    val context = new DefaultRenderContext(path, engine, new PrintWriter(writer))
    template.render(context)
    IOUtils.toInputStream(writer.toString)
  }

}

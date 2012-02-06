package nl.flotsam.monkeyman.decorator.haml

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import java.io.{PrintWriter, StringWriter}
import org.apache.commons.io.IOUtils
import nl.flotsam.monkeyman.Resource
import org.fusesource.scalate.{Template, DefaultRenderContext, TemplateEngine}
import org.apache.commons.io.FilenameUtils._

class ScalateToHtmlDecoration(resource: Resource, template: Template, engine: TemplateEngine, allResources: () => Seq[Resource])
  extends ResourceDecoration(resource)
{
  override val path = removeExtension(resource.path) + ".html"

  override def contentType = "text/html"

  override def open = {
    val writer = new StringWriter
    val context = new DefaultRenderContext(path, engine, new PrintWriter(writer))
    context.attributes("allResources") = allResources()
    template.render(context)
    IOUtils.toInputStream(writer.toString)
  }

}

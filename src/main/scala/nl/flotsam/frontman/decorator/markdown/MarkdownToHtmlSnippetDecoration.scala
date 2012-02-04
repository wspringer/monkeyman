package nl.flotsam.frontman.decorator.markdown

import nl.flotsam.frontman.Resource
import nl.flotsam.frontman.decorator.ResourceDecoration
import org.pegdown.PegDownProcessor
import org.apache.commons.io.IOUtils
import nl.flotsam.frontman.util.Closeables._

case class MarkdownToHtmlSnippetDecoration(resource: Resource) extends ResourceDecoration(resource) {

  override def contentType = "application/vnd.frontman.snippet"

  override def open =
    using(resource.open) {
      in =>
        val processor = new PegDownProcessor
        val processed = processor.markdownToHtml(IOUtils.toString(in, "UTF-8"))
        IOUtils.toInputStream(processed)
    }

}

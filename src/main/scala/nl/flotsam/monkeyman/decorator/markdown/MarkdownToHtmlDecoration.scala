/*
 * Monkeyman static web site generator
 * Copyright (C) 2012  Wilfred Springer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package nl.flotsam.monkeyman.decorator.markdown

import nl.flotsam.monkeyman.Resource
import nl.flotsam.monkeyman.decorator.ResourceDecoration
import nl.flotsam.monkeyman.util.Closeables._
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine, Template}
import org.apache.commons.io.{FilenameUtils, IOUtils}
import java.io.{PrintWriter, StringWriter}
import org.pegdown.ast.{TextNode, HeaderNode}
import org.pegdown.{LinkRenderer, ToHtmlSerializer, PegDownProcessor}

case class MarkdownToHtmlDecoration(resource: Resource, template: Option[Template], engine: TemplateEngine, allResources: () => Seq[Resource])
  extends ResourceDecoration(resource) {

  lazy val (extractedTitle, html) = {
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

  override def title = resource.title.orElse(extractedTitle)

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
      context.attributes("allResources") = allResources()
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

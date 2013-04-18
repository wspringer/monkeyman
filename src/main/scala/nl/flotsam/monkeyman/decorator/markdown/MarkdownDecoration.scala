/*
 * Monkeyman static web site generator
 * Copyright (C) 2013  Wilfred Springer
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
import org.apache.commons.io.{FilenameUtils, IOUtils}
import org.pegdown.ast.{SimpleNode, TextNode, HeaderNode}
import org.pegdown.{LinkRenderer, ToHtmlSerializer, PegDownProcessor}
import nl.flotsam.monkeyman.util.Logging

case class MarkdownDecoration(resource: Resource, sections: Boolean)
  extends ResourceDecoration(resource) with Logging {

  def extractedTitle = extract._1

  def html = extract._2

  def extract = using(resource.open) {
    in =>
      val markdown = IOUtils.toString(in, "UTF-8")
      val processor = new PegDownProcessor
      val rootNode = processor.parseMarkdown(markdown.toCharArray)
      val visitor = new TitleExtractingToHtmlSerializer(new LinkRenderer)
      val html =
        if (sections) "<section>" + visitor.toHtml(rootNode) + "</section>"
        else visitor.toHtml(rootNode)
      val title = visitor.title
      (title, html)
  }

  override def title = resource.title.orElse(extractedTitle)

  override val contentType = "text/x-html-fragment"

  override def path = {
    if (!FilenameUtils.getExtension(resource.path).isEmpty) FilenameUtils.removeExtension(resource.path) + ".frag"
    else resource.path
  }

  override def open = IOUtils.toInputStream(html, "UTF-8")

  override def asHtmlFragment = Some(html)

  class TitleExtractingToHtmlSerializer(linkRenderer: LinkRenderer) extends ToHtmlSerializer(linkRenderer) {
    var inheader = false
    var title: Option[String] = None

    override def visit(node: HeaderNode) {
      if (title.isEmpty) {
        inheader = true
        visitChildren(node)
        inheader = false
      } else {
        super.visit(node)
      }
    }

    override def visit(node: TextNode) {
      if (inheader && title.isEmpty) {
        title = Some(node.getText)
      } else {
        super.visit(node)
      }
    }

    override def visit(node: SimpleNode) {
      node.getType match {
        case SimpleNode.Type.HRule if (sections) =>
          printer.println.print("</section><section>")
        case _ => super.visit(node)
      }
    }


  }

}

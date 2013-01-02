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

package nl.flotsam.monkeyman.loader.resize

import nl.flotsam.monkeyman.Resource
import nl.flotsam.monkeyman.util.Closeables
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.ByteArrayInputStream

class ResizedResource(val path: String,
                      width: Option[Int],
                      height: Option[Int],
                      original: String,
                      allResources: () => List[Resource])
  extends Resource {

  private def resource = allResources().find(_.id == original).get

  def title = resource.title

  def subtitle = resource.subtitle

  def summary = resource.summary

  def pubDateTime = resource.pubDateTime

  def contentType = resource.contentType

  def open =
    if (width.isDefined && height.isDefined) {
      Closeables.using(resource.open) {
        in =>
          val src = ImageIO.read(in)
          val resized = Scalr.resize(src, width.get, height.get, Scalr.OP_ANTIALIAS)
          val buffer = new ByteArrayOutputStream()
          ImageIO.write(resized, FilenameUtils.getExtension(path), buffer)
          new ByteArrayInputStream(buffer.toByteArray)
      }
    } else resource.open

  def tags = resource.tags

  def published = resource.published

  def asHtmlFragment = None

  def id = path

  override def generated = true

}
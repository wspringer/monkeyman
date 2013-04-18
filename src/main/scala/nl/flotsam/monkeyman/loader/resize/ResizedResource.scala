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

package nl.flotsam.monkeyman.loader.resize

import nl.flotsam.monkeyman.Resource
import nl.flotsam.monkeyman.util.Closeables
import javax.imageio.ImageIO
import org.imgscalr.Scalr
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.output.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import org.imgscalr.Scalr.Mode

class ResizedResource(val path: String,
                      width: Option[Int],
                      height: Option[Int],
                      cropped: Boolean = false,
                      original: String,
                      allResources: () => List[Resource])
  extends Resource {

  private def resource = allResources().find(_.id == original).get

  def title = resource.title

  def subtitle = resource.subtitle

  def summary = resource.summary

  def pubDateTime = resource.pubDateTime

  def contentType = resource.contentType

  /**
   * Fix the image in a box.
   *
   * If only height is defined, then resize height
   * If only width is defined, then resize width
   * If width and height is defined, then fit in the given box, which means comparing aspect ratio of both the
   * bounding box and the incoming image. If cropped is set, then do the opposite.
   */
  def open = Closeables.using(resource.open) {
    in =>
      val src = ImageIO.read(in)
      val resized = (width, height) match {
        case (Some(w), None) => Scalr.resize(src, Mode.FIT_TO_WIDTH, w, Scalr.OP_ANTIALIAS)
        case (None, Some(h)) => Scalr.resize(src, Mode.FIT_TO_HEIGHT, h, Scalr.OP_ANTIALIAS)
        case (Some(w), Some(h)) =>
          if (cropped) {
            if (w.toDouble / h.toDouble > src.getWidth.toDouble / src.getHeight.toDouble) {
              val img = Scalr.resize(src, Mode.FIT_TO_WIDTH, w, Scalr.OP_ANTIALIAS)
              img.getSubimage(0, (img.getHeight - h) / 2, w, h)
            } else {
              val img = Scalr.resize(src, Mode.FIT_TO_HEIGHT, h, Scalr.OP_ANTIALIAS)
              img.getSubimage((img.getWidth - w) / 2, 0, w, h)
            }
          } else Scalr.resize(src, w, h, Scalr.OP_ANTIALIAS)
        case _ => throw new IllegalStateException("Got different parameters than expected")
      }
      val buffer = new ByteArrayOutputStream()
      ImageIO.write(resized, FilenameUtils.getExtension(path), buffer)
      new ByteArrayInputStream(buffer.toByteArray)
  }

  def tags = resource.tags

  def published = resource.published

  def asHtmlFragment = None

  def id = path

  override def generated = true

}
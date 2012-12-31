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

import nl.flotsam.monkeyman.{Resource, ResourceListener, ResourceLoader}
import org.apache.commons.io.IOUtils
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import collection.mutable

class ResizingResourceLoader(loader: ResourceLoader) extends ResourceLoader {

  private val ResizedImage = """^([.]*)-([0-9]+)x([0-9]+)\.([.]*)$""".r
  private val extra = mutable.Buffer.empty[ResizedResource]

  def load = {
    val given = loader.load
    extra ++= (for {
      htmlPage <- given.filter(_.contentType == "text/html")
      text = IOUtils.toString(htmlPage.open, "UTF-8")
      parsed = Jsoup.parse(text)
      img <- parsed.select("img").toList
      src = img.attr("src")
      List(base, width, height, ext) <- ResizedImage.unapplySeq(src)
      original <- given.find(_.path == base + "." + ext)
      if (original.contentType.startsWith("image/"))
    } yield new ResizedResource(src, width, height, original))
    given ++ extra
  }

  def register(listener: ResourceListener) {
    loader.register(listener)
  }

}

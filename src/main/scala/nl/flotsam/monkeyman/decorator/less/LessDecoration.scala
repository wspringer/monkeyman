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

package nl.flotsam.monkeyman.decorator.less

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import com.asual.lesscss.LessEngine
import org.apache.commons.io.{IOUtils, FilenameUtils}
import java.net.URL
import nl.flotsam.monkeyman.Resource

class LessDecoration(engine: LessEngine, resource: Resource,  url: URL) extends ResourceDecoration(resource) {

  override lazy val path = FilenameUtils.removeExtension(resource.path) + ".css"

  override def contentType = "text/css"

  override def open = {
    // TODO: This thing behaves weird. It will just kill the thread in some cases, without an exception
    val css = engine.compile(url)
    IOUtils.toInputStream(css, "UTF-8")
  }

}

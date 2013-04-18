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

package nl.flotsam.monkeyman.decorator.zuss

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import nl.flotsam.monkeyman.Resource
import nl.flotsam.monkeyman.util.Closeables
import org.zkoss.zuss.{Locator, Zuss}
import java.io.{StringWriter, FileNotFoundException}
import org.zkoss.zuss.impl.out.BuiltinResolver
import org.apache.commons.io.{IOUtils, FilenameUtils}

class ZussDecoration(resource: Resource) extends ResourceDecoration(resource) {
  
  override def contentType = "text/css"

  override def path = FilenameUtils.removeExtension(resource.path) + ".css"

  override def open = {
    Closeables.using(resource.open) {
      in =>
        val definition = Zuss.parse(in, "UTF-8", NullLocator, FilenameUtils.getName(resource.path))
        val writer = new StringWriter
        Zuss.translate(definition, writer, new BuiltinResolver)
        IOUtils.toInputStream(writer.toString)
    }
  }

  // Need to do something with relative locations
  object NullLocator extends Locator {
    def getResource(name: String) = throw new FileNotFoundException("Failed to find file called " + name)
  }
  
}

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

package nl.flotsam.monkeyman.sink

import nl.flotsam.monkeyman.{SinkFactory, Resource, Sink}
import scala.util.control.Exception._
import java.io.File
import nl.flotsam.monkeyman.util.Closeables._
import org.apache.commons.io.FileUtils
import nl.flotsam.monkeyman.util.Logging

class FileSystemSink(directory: File) extends Sink with Logging {

  directory.mkdirs()

  def receive(resource: Resource) {
    val targetFile = new File(directory, resource.path)
    using(resource.open) {
      info("Generating {}", resource.path)
      FileUtils.copyInputStreamToFile(_, targetFile)
    }
  }
}

object FileSystemSink extends SinkFactory {

  def create(location: String) =
    allCatch.opt {
      val directory =
        if (location.startsWith("~")) new File(System.getProperty("user.home") + location.substring(1))
        else new File(location)
      new FileSystemSink(directory)
    }

}

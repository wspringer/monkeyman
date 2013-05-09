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

package nl.flotsam.monkeyman

import java.io.File
import util.Logging
import org.clapper.argot.ArgotConverters._
import nl.flotsam.monkeyman.sink.{S3Sink, FileSystemSink}

object MonkeymanGenerator extends MonkeymanTool("monkeyman generate") with Logging {

  private val list = parser.flag("l", true, "Only list the pages found.")

  private val sinkFactories: List[SinkFactory] = List(S3Sink, FileSystemSink)

  private def createSink(location: String): Option[Sink] =
    sinkFactories.view.map(_.create(location)).flatten.headOption

  val targetLocation = parser.option[String](List("o", "out"), "LOCATION",
    "The location where generated content will be stored. (Defaults to 'target' directory.")

  def execute(config: MonkeymanConfiguration) {
    if (list.value == Some(true))
      println(config.registry.allResources.par.map {
        resource =>
          resource.contentType + " " + resource.path
      }.mkString("\n"))
    else generate(config, targetLocation.value.getOrElse(new File(workingDir, "target").getAbsolutePath))
  }

  private def generate(config: MonkeymanConfiguration, location: String) {
    createSink(location) match {
      case Some(sink) =>
        for {
          resource <- config.registry.allResources
          if resource.contentType != "application/directory"
        } sink.receive(resource)
      case None =>
        System.err.println("'%s' is not a valid location".format(location))
        System.exit(1)
    }
  }

}

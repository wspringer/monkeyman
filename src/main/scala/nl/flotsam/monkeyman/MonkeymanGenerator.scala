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

package nl.flotsam.monkeyman

import java.io.File
import util.{Closeables, Logging}
import Closeables._
import org.apache.commons.io.FileUtils
import org.clapper.argot.ArgotConverters._

object MonkeymanGenerator extends MonkeymanTool("monkeyman generate") with Logging {

  private val list = parser.flag("l", true, "Only list the pages found.")

  def execute(config: MonkeymanConfiguration) {
    if (list.value == Some(true))
      println(config.registry.allResources.map {
        resource =>
          resource.contentType + " " + resource.path
      }.mkString("\n"))
    else generate(config, targetDir.value.getOrElse(new File(workingDir, "target")))
  }

  private def generate(config: MonkeymanConfiguration, targetDir: File) {
    targetDir.mkdirs()
    for (resource <- config.registry.allResources) {
      val targetFile = new File(targetDir, resource.path)
      using(resource.open) {
        info("Generating {}", resource.path)
        FileUtils.copyInputStreamToFile(_, targetFile)
      }
    }
  }

}

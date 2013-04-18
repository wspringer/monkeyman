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

import org.clapper.argot.ArgotConverters._
import java.io.File
import org.clapper.argot.{ArgotUsageException, ArgotParser}
import eu.medsea.mimeutil.{MimeType, MimeUtil}

abstract class MonkeymanTool(toolName: String) {

  MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.ExtensionMimeDetector")

  val workingDir = new File(System.getProperty("user.dir"))

  val parser = new ArgotParser(toolName)

  val help = parser.flag(List("h", "help"), "Print usage information.")

  val sourceDir = parser.option[File](List("i", "in"), "DIR",
    "The directory to scan for content. (Defaults to 'source' directory.)") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }

  val targetDir = parser.option[File](List("o", "out"), "DIR",
    "The directory for storing the generated pages. (Defaults to 'target' directory.") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }

  val layoutDir = parser.option[File](List("t", "layout"), "DIR",
    "The directory for storing the layout pages. (Defaults to 'layout' directory.") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }

  def directoryBrowsing: Boolean = false

  val sections = parser.flag[Boolean]("sections", false, "Interpret markdown horizontal rules as section breaks.")

  def main(args: Array[String]) {
    try {
      parser.parse(args)
      if (help.value == Some(true))
        println(parser.usageString(None))
      else {
        val config = new MonkeymanConfiguration(
          sourceDir = sourceDir.value.getOrElse(new File(workingDir, "source")),
          layoutDir = layoutDir.value.getOrElse(new File(workingDir, "layout")),
          sections = sections.value.getOrElse(false),
          directoryBrowsing = directoryBrowsing
        )
        try {
          if (!config.sourceDir.exists()) {
            System.err.println("Source directory %s does not exist".format(config.sourceDir.getAbsolutePath))
            System.exit(0)
          }
          execute(config)
        } finally {
          config.dispose
        }
      }
      System.exit(0)
    } catch {
      case aue: ArgotUsageException =>
        parser.usageString(Some(aue.getMessage))
        System.exit(0)
    }
  }

  def execute(config: MonkeymanConfiguration): Unit

}

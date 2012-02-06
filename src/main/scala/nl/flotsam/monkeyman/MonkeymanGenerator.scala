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

import org.clapper.argot.{ArgotUsageException, ArgotParser}
import org.clapper.argot.ArgotConverters._
import java.io.File
import util.Closeables
import Closeables._
import org.apache.commons.io.FileUtils


object MonkeymanGenerator {

  private val parser = new ArgotParser("monkeyman generate")
  
  private val list = parser.flag("l", true, "Only list the pages found.")
  
  private val help = parser.flag(List("h", "help"), "Print usage information.")
  
  private val sourceDir = parser.option[File](List("i", "in"), "DIR",
    "The directory to scan for content. (Defaults to 'source' directory.)") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }
  
  private val targetDir = parser.option[File](List("o", "out"), "DIR",
    "The directory for storing the generated pages. (Defaults to 'target' directory.") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }
  
  private val layoutDir = parser.option[File](List("t", "layout"), "DIR",
    "The directory for storing the layout pages. (Defaults to 'layout' directory.") {
    (name, opt) =>
      if (name.startsWith("~")) new File(System.getProperty("user.home") + name.substring(1))
      else new File(name)
  }

  def main(args: Array[String]) {
    try {
      parser.parse(args)
      val config = new MonkeymanConfiguration(
        sourceDir = sourceDir.value.getOrElse(new File("source")),
        layoutDir = layoutDir.value.getOrElse(new File("layout"))
      )
      if (help.value == Some(true))
        println(parser.usageString(None))
      else if (list.value == Some(true)) 
        println(config.resourceLoader.load.map{
          resource =>
            resource.contentType + " " + resource.path
        }.mkString("\n"))
      else generate(config, targetDir.value.getOrElse(new File("target")))
      System.exit(0)
    } catch {
      case aue: ArgotUsageException =>
        parser.usageString(Some(aue.getMessage))
        System.exit(1)
    }
  }
  
  private def generate(config: MonkeymanConfiguration, targetDir: File) {
    targetDir.mkdirs()
    for (resource <- config.resourceLoader.load) {
      val targetFile = new File(targetDir, resource.path)
      using(resource.open) {
        FileUtils.copyInputStreamToFile(_, targetFile)
      }
    }
  }

}

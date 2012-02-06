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

package nl.flotsam.monkeyman.decorator.yaml

import nl.flotsam.monkeyman.Resource
import nl.flotsam.monkeyman.util.Closeables._
import scala.util.control.Exception._
import org.apache.commons.io.IOUtils
import collection.JavaConversions._
import nl.flotsam.monkeyman.decorator.ResourceDecoration

/**
 * Mimics YAML front matter extraction. Not really YAML, but who cares?
 */
class YamlFrontmatterDecoration(resource: Resource) extends ResourceDecoration(resource) {

  type AttributeSet = Map[String, String]
  
  lazy val (attributes, content) = {
    allCatch.opt(using(resource.open)(IOUtils.toString(_, "UTF-8"))) match {
      case Some(str) =>
        extractAttributes(str)
      case None => 
        (Map.empty[String, String], None)
    }
  }

  override def title = {
    val title = attributes.get("title")
    title.orElse(resource.title)
  }

  override def tags = 
    resource.tags ++ attributes.get("tags").map(_.split(",")).getOrElse(Array.empty)

  override def open = {
    if (content.isDefined) IOUtils.toInputStream(content.get, "UTF-8")
    else resource.open
  }

  private def extractAttributes(str: String): (Map[String, String], Option[String]) = {
    val lines = str.lines.toList
    lines.headOption match {
      case Some(line) if line.trim == "---" =>
        val (settings, remainder) = lines.tail.span(_ != "---")
        val attributes = (for {
          setting <- settings
          (key, value) = setting.span(_ != ':')
        } yield {
          (key.trim, value.tail.trim)
        }).toMap
        (attributes, Some(remainder.tail.mkString("\n")))
      case _ =>
        (Map.empty, None)
    }
  }

  override def toString = this.getClass.getName + "(" + resource.toString + ")"
  
}

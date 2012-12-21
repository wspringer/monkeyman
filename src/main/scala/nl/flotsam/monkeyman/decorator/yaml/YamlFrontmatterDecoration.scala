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
import nl.flotsam.monkeyman.decorator.ResourceDecoration
import nl.flotsam.monkeyman.util.Logging
import org.joda.time.format.{DateTimeFormatter, DateTimeFormat}
import org.joda.time.LocalDateTime
import org.yaml.snakeyaml.Yaml
import collection.JavaConversions._
import java.util.Date

/**
 * Mimics YAML front matter extraction. Not really YAML, but who cares?
 */
class YamlFrontmatterDecoration(resource: Resource) extends ResourceDecoration(resource) with Logging {

  private val pattern1 = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm")
  private val pattern2 = DateTimeFormat.forPattern("yyyy-MM-dd")

  private def parse(str: String): Option[LocalDateTime] = {
    val patterns = Stream(pattern1, pattern2)
    
    def tryParse(pattern: DateTimeFormatter) =
      allCatch.opt(pattern.parseLocalDateTime(str))
    
    patterns.flatMap(tryParse).headOption
  }

  def attributes = extract._1

  def content = extract._2

  def extract =
    allCatch.opt(using(resource.open)(IOUtils.toString(_, "UTF-8"))) match {
      case Some(str) =>
        extractAttributes(str)
      case None => 
        (Map.empty[String, String], None)
    }

  override def title = {
    val title = attributes.get("title").map(_.asInstanceOf[String])
    title.orElse(resource.title)
  }

  override def subtitle = attributes.get("subtitle").map(_.asInstanceOf[String]).orElse(resource.title)

  override def summary = attributes.get("summary").map(_.asInstanceOf[String]).orElse(resource.summary)

  override def published = attributes.get("published").map(_.asInstanceOf[Boolean]).getOrElse(resource.published)

  override def tags =
    resource.tags ++ attributes.get("tags").map(_.asInstanceOf[String].split(",").map(_.trim)).getOrElse(Array.empty)

  override def pubDateTime = attributes.get("pubDateTime").map(_.asInstanceOf[Date]) match {
    case Some(date) =>
      LocalDateTime.fromDateFields(date)
    case None =>
      resource.pubDateTime
  }

  override def open = {
    if (content.isDefined) IOUtils.toInputStream(content.get, "UTF-8")
    else resource.open
  }

  private def extractAttributes(str: String): (Map[String, Any], Option[String]) = {
    val lines = str.lines.toList
    lines.headOption match {
      case Some(line) if line.trim == "---" =>
        val (settings, remainder) = lines.tail.span(_ != "---")
        val yaml = new Yaml
        val attributes =
          yaml.loadAs[java.util.Map[String, Any]](settings.mkString("\n"), classOf[java.util.Map[String,Any]]).toMap
        (attributes, Some(remainder.tail.mkString("\n")))
      case _ =>
        (Map.empty, None)
    }
  }

  override def toString = this.getClass.getName + "(" + resource.toString + ")"
  
}

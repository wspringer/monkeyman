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

import java.io.{FileInputStream, File}
import eu.medsea.mimeutil.{MimeType, MimeUtil}
import collection.JavaConversions
import JavaConversions._
import org.joda.time.LocalDateTime

case class FileSystemResource(baseDir: File, path: String) extends Resource {

  lazy val url = file.toURI.toURL

  lazy val file = new File(baseDir, path)

  lazy val title = None

  val subtitle = None

  val summary = None

  lazy val pubDateTime = new LocalDateTime(file.lastModified())

  lazy val contentType = MimeUtil.getMimeTypes(file).asInstanceOf[java.util.Set[MimeType]].head.toString

  def open = new FileInputStream(file)

  def tags = Set.empty

  def published = true

  def asHtmlFragment = None

  def id = path

  override def supportsPathRewrite = !file.isDirectory

}

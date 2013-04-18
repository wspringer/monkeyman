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

import java.io.{ByteArrayOutputStream, InputStream}
import org.joda.time.LocalDateTime
import org.apache.commons.io.{IOUtils, FilenameUtils}
import util.Closeables

trait Resource {

  /**
   * A human readable String to refer to this file. Can contain spaces.
   */
  def title: Option[String]

  /**
   * The subtitle of this resource.
   */
  def subtitle: Option[String]
  
  /**
   * A summary of this snippet of this resource.
   */
  def summary: Option[String]

  /**
   * The date from which on this file should be considered published. (Note: that doesn't mean it will stay there
   * forever, it just allows you to hold off publication if you don't want to and have a publication date if that's
   * what you want to display.
   */
  def pubDateTime: LocalDateTime

  /**
   * The type of resource.
   */
  def contentType: String

  /**
   * The bytes.
   */
  def open: InputStream

  /**
   * The path to this resource.
   */
  def path: String

  /**
   * A number of tags associated to this resource. Eases lookups. Can be used for whatever you need.
   */
  def tags: Set[String]

  /**
   * If this resource should be included in the output.
   */
  def published: Boolean

  /**
   * Get an HTML fragment to be included somewhere else.
   */
  def asHtmlFragment: Option[String]

  def asString = {
    val out = new ByteArrayOutputStream()
    Closeables.using(open)(in => IOUtils.copy(in, out))
    new String(out.toByteArray, "UTF-8")
  }

  /**
   * The unique identifier of this resource. Doesn't change during its lifetime.
   */
  def id: String

  def supportsPathRewrite = false

  /**
   * The folder in which a resource resides. (The path without the filename.)
   */
  lazy val folderName = FilenameUtils.getPath(path)

  lazy val fileName = FilenameUtils.getName(path)

  def baseName = FilenameUtils.getBaseName(path)

  def extension = FilenameUtils.getExtension(path)

  /**
   * Gets the siblings from a collection of resources.
   */
  def siblings(resources: Seq[Resource]) = resources.filter(_.folderName == folderName)

  def generated = false
  
}

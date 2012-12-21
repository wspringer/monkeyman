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

package nl.flotsam.monkeyman.decorator.directory

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import nl.flotsam.monkeyman.Resource
import org.apache.commons.io.FilenameUtils
import java.io.ByteArrayInputStream

class DirectoryBrowsingDecoration(resource: Resource, allResources: () => Seq[Resource]) extends ResourceDecoration(resource) {

  override def contentType = "text/plain"

  override def title = Some("Index")

  override def open = {
    val content = (for {
      res <- allResources()
      if (res.contentType != "application/directory" && FilenameUtils.getPath(res.path) == resource.path + "/")
    } yield " * " + FilenameUtils.getName(res.path))
    new ByteArrayInputStream(content.mkString("\n").getBytes("UTF-8"))
  }

  override def supportsPathRewrite = false

}

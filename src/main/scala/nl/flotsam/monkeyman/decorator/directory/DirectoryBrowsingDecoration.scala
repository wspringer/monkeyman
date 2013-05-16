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

package nl.flotsam.monkeyman.decorator.directory

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import nl.flotsam.monkeyman.Resource
import org.apache.commons.io.{IOUtils, FilenameUtils}
import java.io.{PrintWriter, StringWriter, ByteArrayInputStream}
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine}

class DirectoryBrowsingDecoration(resource: Resource, allResources: () => Seq[Resource], engine: TemplateEngine)
  extends ResourceDecoration(resource)
{

  override def contentType = "text/html"

  override def title = Some("Index")

  override def open = {
    val result = engine.layout("__default_index.scaml", Map(
      "resources" -> (for {
        res <- allResources()
        if (res.path != this.path && FilenameUtils.getPathNoEndSeparator(res.path) == resource.path)
      } yield res).toList,
      "allResources" -> allResources(),
      "id" -> id
    ))
    IOUtils.toInputStream(result, "UTF-8")
  }

  override def supportsPathRewrite = false

}

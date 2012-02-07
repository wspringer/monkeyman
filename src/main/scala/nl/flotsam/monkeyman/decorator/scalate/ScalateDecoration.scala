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

package nl.flotsam.monkeyman.decorator.scalate

import nl.flotsam.monkeyman.decorator.ResourceDecoration
import java.io.{PrintWriter, StringWriter}
import org.apache.commons.io.IOUtils
import nl.flotsam.monkeyman.Resource
import org.fusesource.scalate.{Template, DefaultRenderContext, TemplateEngine}
import org.apache.commons.io.FilenameUtils._

class ScalateDecoration(resource: Resource, template: Template, engine: TemplateEngine, allResources: () => Seq[Resource])
  extends ResourceDecoration(resource)
{
  override val path = removeExtension(resource.path) + ".html"

  override def contentType = "text/html"

  override def open = {
    val writer = new StringWriter
    val context = new DefaultRenderContext(path, engine, new PrintWriter(writer))
    context.attributes("allResources") = allResources()
    template.render(context)
    IOUtils.toInputStream(writer.toString)
  }

}

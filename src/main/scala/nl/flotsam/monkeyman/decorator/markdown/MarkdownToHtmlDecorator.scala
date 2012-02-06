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

package nl.flotsam.monkeyman.decorator.markdown

import org.fusesource.scalate.TemplateEngine
import nl.flotsam.monkeyman.{LayoutResolver, Resource, ResourceDecorator}

class MarkdownToHtmlDecorator(engine: TemplateEngine, layoutResolver: LayoutResolver, allResources: () => Seq[Resource]) extends ResourceDecorator {
  
  def decorate(resource: Resource) = {
    if (resource.contentType == "text/x-web-markdown" || resource.path.endsWith(".md"))
      new MarkdownToHtmlDecoration(resource, layoutResolver.resolve(resource.path), engine, allResources)
    else resource
  }
  
}

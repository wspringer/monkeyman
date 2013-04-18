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

import collection.mutable.Map
import util.Logging

class ResourceGenerator(listener: ResourceListener, fn: (Resource) => List[Resource]) extends ResourceListener with Logging {

  private val memory = Map.empty[String, List[Resource]]

  def deleted(id: String) {
    for (resource <- memory.get(id).flatten) {
      listener.deleted(id)
    }
  }

  def modified(resource: Resource) {
    val resources = fn(resource)
    for (prev <- memory.get(resource.id).flatten) listener.deleted(prev.id)
    if (resources.isEmpty) {
      memory -= resource.id
    } else {
      memory += resource.id -> resources
      for (nxt <- resources) listener.added(resource)
    }
  }

  def added(resource: Resource) {
    val resources = fn(resource)
    if (!resources.isEmpty) {
      memory += resource.id -> resources
      for (nxt <- resources) listener.added(nxt)
    }
  }

}

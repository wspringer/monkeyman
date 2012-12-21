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

package nl.flotsam.monkeyman.decorator.registry

import nl.flotsam.monkeyman.{ResourceListener, Resource, ResourceDecorator}
import nl.flotsam.monkeyman.util.Logging


class RegistryDecorator extends ResourceDecorator with ResourceListener with Logging {

  private val resourcesById = collection.mutable.Map.empty[String,  Resource]
  val resourceByPath = collection.mutable.Map.empty[String,  Resource]
  
  def allResources: List[Resource] = resourcesById.values.toList
  
  def decorate(resource: Resource) = {
    resourcesById += resource.id -> resource
    resourceByPath += resource.path -> resource
    resource
  }

  def deleted(id: String) {
    resourcesById.get(id) match {
      case Some(resource) => 
        info("Removed {}", resource.path)
        resourcesById -= id
        resourceByPath -= resource.path
      case None =>
        warn("Entering illegal state: can't find resource with id " + id)
    }
  }

  def added(resource: Resource) {
    info("Added {}", resource.path)
    resourcesById += resource.id -> resource
    resourceByPath += resource.path -> resource
  }

  def modified(resource: Resource) {
    info("Modified {}", resource.path)
    resourcesById += resource.id -> resource
    resourceByPath += resource.path -> resource
  }

}

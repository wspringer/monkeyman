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

import util.Logging
import collection.mutable

case class Registry(loader: ResourceLoader)
  extends ResourceListener
  with Logging
{

  private val listeners = mutable.Buffer.empty[ResourceListener]

  private lazy val resources = loader.load.toBuffer
  def allResources = resources.toList
  private lazy val resourceById = collection.mutable.Map(resources.map(resource => resource.id -> resource): _*)
  lazy val resourceByPath = collection.mutable.Map(resources.map(resource => resource.path -> resource): _*)

  loader.register(this)

  def register(listener: ResourceListener) {
    resources.foreach(listener.added)
    listeners += listener
  }

  def deleted(id: String) {
    val resource = resourceById.get(id)
    resource match {
      case Some(resource) =>
        listeners.foreach(_.deleted(resource.id))
        info("Removed {}", resource.id)
        resourceById -= id
        resourceByPath -= resource.path
        resources -= resource
      case None =>
        info("Failed to find {}", id)
    }
  }

  def added(resource: Resource) {
    listeners.foreach(_.added(resource))
    info("Added {} ({})", resource.id, resource.path)
    resourceById += resource.id -> resource
    resourceByPath += resource.path -> resource
    resources += resource
  }

  def modified(resource: Resource) {
    listeners.foreach(_.modified(resource))
    info("Modified {} ({})", resource.id, resource.path)
    resources.find(_.id == resource.id).map(resources -=)
    resourceById.get(resource.id).map(previous => if (previous.path != resource.path) {
      info("Changing path of {} to {}", previous.id, resource.path)
      resourceByPath -= previous.path
    })
    resourceById += resource.id -> resource
    resourceByPath += resource.path -> resource
    resources += resource
  }

}

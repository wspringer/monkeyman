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


class ClasspathResourceLoader(paths: Seq[String], loader: ResourceLoader) extends ResourceLoader {
  
  private val resources = paths.map(path => ClasspathResource(path))
  
  def load = {
    val loaded = loader.load
    val ids = loaded.map(_.id)
    resources.filter(resource => !ids.contains(resource.id)) ++ loaded
  } 

  def register(listener: ResourceListener) {
    loader.register(new ResourceListener {
      def deleted(id: String) {
        resources.find(_.id == id) match {
          case Some(resource) =>
            listener.modified(resource)
          case None =>
            listener.deleted(id)
        }
      }

      def modified(resource: Resource) {
        listener.modified(resource)
      }

      def added(resource: Resource) {
        resources.find(_.id == resource.id) match {
          case Some(resource) => listener.modified(resource)
          case None => listener.added(resource)
        }
      }
    })
  }
  


}

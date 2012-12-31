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

package nl.flotsam.monkeyman.loader.resize

import nl.flotsam.monkeyman.Resource

class ResizedResource(val path: String, width: String, height: String, resource: Resource) extends Resource {
  def title = resource.title

  def subtitle = resource.subtitle

  def summary = resource.summary

  def pubDateTime = resource.pubDateTime

  def contentType = resource.contentType

  def open = resource.open

  def tags = resource.tags

  def published = resource.published

  def asHtmlFragment = None

  def id = path
}
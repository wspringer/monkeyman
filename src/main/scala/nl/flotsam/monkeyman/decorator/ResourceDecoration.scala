package nl.flotsam.monkeyman.decorator

import nl.flotsam.monkeyman.Resource

class ResourceDecoration(resource: Resource) extends Resource {

  def title = resource.title

  def pubDate = resource.pubDate

  def contentType = resource.contentType

  def open = resource.open

  def path = resource.path

  def tags = resource.tags

}

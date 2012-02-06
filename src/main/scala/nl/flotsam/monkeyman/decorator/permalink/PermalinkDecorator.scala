package nl.flotsam.monkeyman.decorator.permalink

import nl.flotsam.monkeyman.{Resource, ResourceDecorator}


object PermalinkDecorator extends ResourceDecorator {
  def decorate(resource: Resource) = new PermalinkDecoration(resource)
}

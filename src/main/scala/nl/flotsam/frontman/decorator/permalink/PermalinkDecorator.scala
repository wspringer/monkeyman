package nl.flotsam.frontman.decorator.permalink

import nl.flotsam.frontman.{Resource, ResourceDecorator}


object PermalinkDecorator extends ResourceDecorator {
  def decorate(resource: Resource) = new PermalinkDecoration(resource)
}

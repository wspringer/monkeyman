package nl.flotsam.monkeyman.decorator.registry

import collection.mutable.Buffer
import nl.flotsam.monkeyman.{Resource, ResourceDecorator}


class RegistryDecorator extends ResourceDecorator {

  private val resources = Buffer.empty[Resource]

  def allResources: List[Resource] = resources.toList
  
  def decorate(resource: Resource) = {
    resources += resource
    resource
  }
}

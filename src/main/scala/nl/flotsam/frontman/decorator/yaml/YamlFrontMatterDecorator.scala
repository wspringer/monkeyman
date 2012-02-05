package nl.flotsam.frontman.decorator.yaml

import nl.flotsam.frontman.{Resource, ResourceDecorator}


class YamlFrontmatterDecorator(included: (Resource) => Boolean = { _.path.endsWith(".md") }) extends ResourceDecorator {
  
  def decorate(resource: Resource) =
    if (included(resource)) new YamlFrontmatterDecoration(resource)
    else resource
  
}

package nl.flotsam.monkeyman.decorator.yaml

import nl.flotsam.monkeyman.{Resource, ResourceDecorator}


class YamlFrontmatterDecorator(included: (Resource) => Boolean = { _.path.endsWith(".md") }) extends ResourceDecorator {
  
  def decorate(resource: Resource) =
    if (included(resource)) new YamlFrontmatterDecoration(resource)
    else resource
  
}

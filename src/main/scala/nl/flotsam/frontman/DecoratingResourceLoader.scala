package nl.flotsam.frontman

class DecoratingResourceLoader(loader: ResourceLoader, decorators: ResourceDecorator*) extends ResourceLoader {

  def load = loader.load.map(decorate)
  
  def decorate(resource: Resource): Resource =
    decorators.foldLeft(resource)((resource, decorator) => decorator.decorate(resource))

}

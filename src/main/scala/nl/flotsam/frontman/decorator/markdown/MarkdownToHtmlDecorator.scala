package nl.flotsam.frontman.decorator.markdown

import org.fusesource.scalate.TemplateEngine
import nl.flotsam.frontman.{LayoutResolver, Resource, ResourceDecorator}

class MarkdownToHtmlDecorator(engine: TemplateEngine, layoutResolver: LayoutResolver) extends ResourceDecorator {
  
  def decorate(resource: Resource) = {
    if (resource.contentType == "text/x-web-markdown" || resource.path.endsWith(".md"))
      new MarkdownToHtmlDecoration(resource, layoutResolver.resolve(resource.path), engine)
    else resource
  }
  
}

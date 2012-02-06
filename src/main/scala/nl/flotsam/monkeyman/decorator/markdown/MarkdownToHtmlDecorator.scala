package nl.flotsam.monkeyman.decorator.markdown

import org.fusesource.scalate.TemplateEngine
import nl.flotsam.monkeyman.{LayoutResolver, Resource, ResourceDecorator}

class MarkdownToHtmlDecorator(engine: TemplateEngine, layoutResolver: LayoutResolver, allResources: () => Seq[Resource]) extends ResourceDecorator {
  
  def decorate(resource: Resource) = {
    if (resource.contentType == "text/x-web-markdown" || resource.path.endsWith(".md"))
      new MarkdownToHtmlDecoration(resource, layoutResolver.resolve(resource.path), engine, allResources)
    else resource
  }
  
}

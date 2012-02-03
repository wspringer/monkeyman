package nl.flotsam.frontman.decorator.markdown

import nl.flotsam.frontman.{Resource, ResourceDecorator}

object MarkdownToHtmlSnippetDecorator extends ResourceDecorator {
  
  def decorate(resource: Resource) = 
    if (resource.contentType == "text/x-web-markdown") new MarkdownToHtmlSnippetDecoration(resource)
    else resource
  
}

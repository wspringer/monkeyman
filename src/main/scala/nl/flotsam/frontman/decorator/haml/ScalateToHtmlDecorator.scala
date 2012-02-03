package nl.flotsam.frontman.decorator.haml

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.support.FileTemplateSource
import nl.flotsam.frontman.{FileSystemResource, Resource, ResourceDecorator}
import scala.util.control.Exception._


class ScalateToHtmlDecorator(engine: TemplateEngine) extends ResourceDecorator {

  def decorate(resource: Resource) = resource match {
    case res @ FileSystemResource(_, _) =>
      val source = new FileTemplateSource(res.file, resource.path)
      allCatch.opt(engine.load(source))
        .map(new ScalateToHtmlDecoration(resource, _, engine))
        .getOrElse(resource)
    case _ => resource
  } 
  

}

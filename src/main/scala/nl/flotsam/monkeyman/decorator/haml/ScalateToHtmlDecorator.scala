package nl.flotsam.monkeyman.decorator.haml

import org.fusesource.scalate.TemplateEngine
import org.fusesource.scalate.support.FileTemplateSource
import nl.flotsam.monkeyman.{FileSystemResource, Resource, ResourceDecorator}
import scala.util.control.Exception._


class ScalateToHtmlDecorator(engine: TemplateEngine, allResources: () => Seq[Resource]) extends ResourceDecorator {

  def decorate(resource: Resource) = resource match {
    case res @ FileSystemResource(_, _) =>
      val source = new FileTemplateSource(res.file, resource.path)
      allCatch.opt(engine.load(source))
        .map {
          template =>
            new ScalateToHtmlDecoration(resource, template, engine, allResources)
        }.getOrElse(resource)
    case _ => resource
  } 
  

}

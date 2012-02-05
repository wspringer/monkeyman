package nl.flotsam.frontman.decorator.yaml

import nl.flotsam.frontman.Resource
import nl.flotsam.frontman.util.Closeables._
import scala.util.control.Exception._
import org.apache.commons.io.IOUtils
import collection.JavaConversions._
import nl.flotsam.frontman.decorator.ResourceDecoration

/**
 * Mimics YAML front matter extraction. Not really YAML, but who cares?
 */
class YamlFrontmatterExtractingDecorator(resource: Resource) extends ResourceDecoration(resource) {

  type AttributeSet = Map[String, String]
  
  lazy val (attributes, content) = {
    allCatch.opt(using(resource.open)(IOUtils.toString(_, "UTF-8"))) match {
      case Some(str) =>
        extractAttributes(str)
      case None => 
        (Map.empty[String, String], None)
    }
  }

  override def label =
    attributes.get("title").orElse(resource.label)

  override def tags = 
    resource.tags ++ attributes.get("tags").map(_.split(",")).getOrElse(Array.empty)

  private def extractAttributes(str: String): (Map[String, String], Option[String]) = {
    val lines = str.lines.toStream
    lines.headOption match {
      case Some(line) if line.trim == "---" =>
        val (settings, remainder) = lines.span(_ != "---")
        val attributes = (for {
          setting <- settings
          (key, value) = setting.span(_ != ':')
        } yield (key.trim, value.tail.trim)).toMap
        (attributes, Some(remainder.tail.mkString("\n")))
      case _ =>
        (Map.empty, None)
    }
  }

}

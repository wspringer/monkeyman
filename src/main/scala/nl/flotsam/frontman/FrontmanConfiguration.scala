package nl.flotsam.frontman

import decorator.haml.ScalateToHtmlDecorator
import java.io.File
import org.fusesource.scalate.TemplateEngine

class FrontmanConfiguration(baseDir: File) {

  private val templateEngine =
    new TemplateEngine(List(baseDir))

  private val fileSystemResourceLoader = 
    new FileSystemResourceLoader(baseDir)
  
  val resourceLoader = new DecoratingResourceLoader(fileSystemResourceLoader,
    new ScalateToHtmlDecorator(templateEngine)
  )



}

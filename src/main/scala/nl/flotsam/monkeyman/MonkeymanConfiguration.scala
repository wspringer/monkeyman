package nl.flotsam.monkeyman

import decorator.haml.ScalateToHtmlDecorator
import decorator.markdown.MarkdownToHtmlDecorator
import decorator.permalink.PermalinkDecorator
import decorator.registry.RegistryDecorator
import decorator.yaml.YamlFrontmatterDecorator
import java.io.File
import org.apache.commons.io.FilenameUtils._
import org.fusesource.scalate.{Binding, Template, TemplateEngine}

class MonkeymanConfiguration(sourceDir: File, layoutDir: File) {

  private val layoutFileName = "layout"
  
  private val templateEngine =
    new TemplateEngine(List(layoutDir, sourceDir))

  private val fileSystemResourceLoader = 
    new FileSystemResourceLoader(sourceDir)

  private val layoutResolver = new LayoutResolver {
    def resolve(path: String) =
      tryLoadTemplate(new File(layoutDir, getPath(path)))
  }

  val registryDecorator = new RegistryDecorator
  templateEngine.bindings = new Binding(
    name = "allResources",
    className = "Seq[nl.flotsam.monkeyman.Resource]",
    defaultValue = Some("Seq.empty[nl.flotsam.monkeyman.Resource]")
  ) :: templateEngine.bindings

  
  val resourceLoader = new DecoratingResourceLoader(fileSystemResourceLoader,
    new YamlFrontmatterDecorator(),
    new MarkdownToHtmlDecorator(templateEngine, layoutResolver, registryDecorator.allResources _),
    new ScalateToHtmlDecorator(templateEngine, registryDecorator.allResources _),
    PermalinkDecorator,
    registryDecorator
  )
  
  private def tryLoadTemplate(dir: File): Option[Template] = {
    val files = 
      TemplateEngine.templateTypes.view.map(ext => new File(dir, layoutFileName + "." + ext))
    files.find(_.exists()) match {
      case Some(file) =>
        Some(templateEngine.load(file))
      case None =>
        if (dir != layoutDir) tryLoadTemplate(dir.getParentFile)
        else None
    }
    
  }

}

/*
 * Monkeyman static web site generator
 * Copyright (C) 2012  Wilfred Springer
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package nl.flotsam.monkeyman

import decorator.directory.DirectoryBrowsingDecorator
import decorator.less.LessDecorator
import decorator.markdown.MarkdownDecorator
import decorator.permalink.PermalinkDecorator
import decorator.scalate.ScalateDecorator
import decorator.snippet.{SnippetDecoration, SnippetDecorator}
import decorator.yaml.YamlFrontmatterDecorator
import decorator.zuss.ZussDecorator
import java.io.{InputStream, OutputStream, ByteArrayInputStream, File}
import loader.resize.ResizedResource
import org.apache.commons.io.FilenameUtils._
import org.fusesource.scalate.{Binding, Template, TemplateEngine}
import org.fusesource.scalate.util.{ResourceLoader => ScalateResourceLoader}
import org.fusesource.scalate.support.URLTemplateSource
import org.apache.commons.io.{FilenameUtils, IOUtils}
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import collection.mutable
import scala.util.control.Exception.allCatch
import java.net.URL
import util.Closeables

case class MonkeymanConfiguration(sourceDir: File,
                                  layoutDir: File,
                                  sections: Boolean = false,
                                  directoryBrowsing: Boolean = false) {

  private val layoutFileName = "layout"

  private val sourceDirectories = List(layoutDir, sourceDir)

  private val templateEngine =
    new TemplateEngine(sourceDirectories)

  private val defaultLoader = templateEngine.resourceLoader
  templateEngine.resourceLoader = new ScalateResourceLoader() {
    def resource(uri: String) =
      if (uri == "__default.scaml") Some(new URLTemplateSource(getClass.getResource("/layout.scaml")))
      else defaultLoader.resource(uri)
  }

  private val fileSystemResourceLoader =
    new FileSystemResourceLoader(sourceDir)

  private val layoutResolver = new LayoutResolver {
    def resolve(path: String) =
      tryLoadTemplate(new File(layoutDir, getPath(path)))
  }

  def dispose {
    fileSystemResourceLoader.dispose
  }

  templateEngine.importStatements = "import nl.flotsam.monkeyman.scalate.Imports._" ::
    "import org.joda.time.LocalDateTime" ::
    templateEngine.importStatements

  templateEngine.bindings = new Binding(
    name = "allResources",
    className = "Seq[nl.flotsam.monkeyman.Resource]",
    defaultValue = Some("Seq.empty[nl.flotsam.monkeyman.Resource]")
  ) :: new Binding(
    name = "title",
    className = "Option[String]",
    defaultValue = Some("None")
  ) :: new Binding(
    name = "subtitle",
    className = "Option[String]",
    defaultValue = Some("None")
  ) :: new Binding(
    name = "summary",
    className = "Option[String]",
    defaultValue = Some("None")
  ) :: new Binding(
    name = "body",
    className = "String",
    defaultValue = Some( """"No body"""")
  ) :: new Binding(
    name = "tags",
    className = "Set[String]",
    defaultValue = Some("Set.empty[String]")
  ) :: new Binding(
    name = "pubDateTime",
    className = "org.joda.time.LocalDateTime",
    defaultValue = Some("LocalDateTime.now")
  ) :: new Binding(
    name = "id",
    className = "String",
    defaultValue = None
  ) :: templateEngine.bindings


  val registry =
    new Registry(
      new DecoratingResourceLoader(
        new ClasspathResourceLoader(Seq("favicon.ico", "monkeyman/logo.png", "monkeyman/monkeyman.less"),
          fileSystemResourceLoader),
        List(
          if (directoryBrowsing) Some(new DirectoryBrowsingDecorator(allResources _)) else None,
          Some(new LessDecorator),
          Some(new ZussDecorator),
          Some(new YamlFrontmatterDecorator()),
          Some(new MarkdownDecorator(sections)),
          Some(new SnippetDecorator(layoutResolver, templateEngine, allResources _)),
          Some(new ScalateDecorator(templateEngine, allResources _)),
          Some(PermalinkDecorator)
        ).flatten
      )
    )

  def allResources: List[Resource] = registry.allResources

  def resized(resource: Resource) = {
    val ResizedImage = """^(.*)-([0-9]+|_)x([0-9]+|_)([c]?)\.([a-z]+)$""".r
    if (resource.contentType == "text/html") {
      val html = IOUtils.toString(resource.open, "UTF-8")
      val parsed = Jsoup.parse(html)
      (for {
        img <- parsed.select("img").toList
        src = img.attr("src")
        cleaned = if (src.startsWith("./")) src.substring(2) else src
        if !allResources.exists(_.path == cleaned)
        List(base, width, height, indicator, ext) <- ResizedImage.unapplySeq(cleaned)
      } yield new ResizedResource(
          src,
          allCatch.opt(width.toInt),
          allCatch.opt(height.toInt),
          indicator == "c",
          base + "." + ext,
          allResources _
        )).toList ++
        (for {
          img <- parsed.select("meta[property=og:image]").toList
          src = new URL(img.attr("content")).getPath.substring(1)
          List(base, width, height, indicator, ext) <- ResizedImage.unapplySeq(src)
        } yield new ResizedResource(
            src,
            allCatch.opt(width.toInt),
            allCatch.opt(height.toInt),
            indicator == "c",
            base + "." + ext,
            allResources _
          )).toList
    } else List.empty
  }

  def imagefragment(resource: Resource) = {
    if (resource.contentType == "text/html") {
      val html = IOUtils.toString(resource.open, "UTF-8")
      val parsed = Jsoup.parse(html)
      (for {
        anchor <- parsed.select("a").toList
        href = anchor.attr("href")
        path = FilenameUtils.concat(resource.folderName, href)
        if path.endsWith(".html") && !allResources.exists(_.path == path)
        directory = FilenameUtils.getPath(path)
        basename = FilenameUtils.getBaseName(path)
        addressed = FilenameUtils.concat(directory, basename)
        img <- allResources.find(_.path == addressed)
        if img.contentType.startsWith("image")
      } yield new SnippetDecoration(new Resource {
        def title = img.title
        def subtitle = img.subtitle
        def summary = img.summary
        def pubDateTime = img.pubDateTime
        def contentType = "text/x-html-fragment"
        def open = new ByteArrayInputStream(asHtmlFragment.get.getBytes("UTF-8"))
        def path = addressed + ".frag"
        def tags = img.tags
        def published = true
        def asHtmlFragment = Some("""<img src="%s"/>""".format(img.path))
        def id = path
      }, layoutResolver, templateEngine, allResources _)).toList
    } else List.empty
  }

  def transpile(resource: Resource) = {
    if (resource.path.endsWith(".js.coffee")) {
      import scala.sys.process._
      var result: String = ""
      val coffee = Seq("coffee", "-s", "-p")
      def feed(out: OutputStream) { Closeables.using(resource.open) {
        in =>
          try { IOUtils.copy(in, out) } finally { out.close() }
      } }
      def append(in: InputStream) {
        try { result = IOUtils.toString(in, "UTF-8") } finally { in.close() }
      }
      def report(in: InputStream) {
        try { IOUtils.copy(in, System.err) } finally { in.close() }
      }
      coffee run new ProcessIO(feed _, append _, report _)
      List(new Resource() {
        def published = resource.published
        def asHtmlFragment = None
        def subtitle = resource.subtitle
        def summary = resource.summary
        def title = resource.title
        def pubDateTime = resource.pubDateTime
        def contentType = "text/javascript"
        def open = new ByteArrayInputStream(result.getBytes("UTF-8"))
        def path = FilenameUtils.removeExtension(resource.path)
        def tags = resource.tags
        def id = FilenameUtils.removeExtension(resource.path)
      })
    } else List.empty
  }

  registry.register(new ResourceGenerator(registry, resized _))
  registry.register(new ResourceGenerator(registry, imagefragment _))
  registry.register(new ResourceGenerator(registry, transpile _))

  private def tryLoadTemplate(dir: File): Template = {
    val files =
      TemplateEngine.templateTypes.view.map(ext => new File(dir, layoutFileName + "." + ext))
    files.find(_.exists()) match {
      case Some(file) => templateEngine.load(file)
      case None =>
        if (dir != layoutDir) tryLoadTemplate(dir.getParentFile)
        else {
          templateEngine.load("__default.scaml")
        }
    }

  }

}

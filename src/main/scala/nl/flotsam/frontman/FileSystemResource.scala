package nl.flotsam.frontman

import org.joda.time.LocalDate
import java.io.{FileInputStream, File}
import eu.medsea.mimeutil.{MimeType, MimeUtil}


case class FileSystemResource(baseDir: File, path: String) extends Resource {

  lazy val file = new File(baseDir, path)

  lazy val label = Some(file.getName)

  lazy val pubDate = Some(new LocalDate(file.lastModified()))

  lazy val contentType = MimeUtil.getMimeTypes(file).asInstanceOf[Seq[MimeType]].head.toString

  def open = new FileInputStream(file)

  def tags = Set.empty

}

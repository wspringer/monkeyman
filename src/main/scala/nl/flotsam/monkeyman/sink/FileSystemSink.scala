package nl.flotsam.monkeyman.sink

import nl.flotsam.monkeyman.{SinkFactory, Resource, Sink}
import scala.util.control.Exception._
import java.io.File
import nl.flotsam.monkeyman.util.Closeables._
import org.apache.commons.io.FileUtils
import nl.flotsam.monkeyman.util.Logging

class FileSystemSink(directory: File) extends Sink with Logging {

  directory.mkdirs()

  def receive(resource: Resource) {
    val targetFile = new File(directory, resource.path)
    using(resource.open) {
      info("Generating {}", resource.path)
      FileUtils.copyInputStreamToFile(_, targetFile)
    }
  }
}

object FileSystemSink extends SinkFactory {

  def create(location: String) =
    allCatch.opt {
      val directory =
        if (location.startsWith("~")) new File(System.getProperty("user.home") + location.substring(1))
        else new File(location)
      new FileSystemSink(directory)
    }

}

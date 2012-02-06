package nl.flotsam.monkeyman

import java.io.File
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import collection.JavaConversions._
import nl.flotsam.monkeyman.ext.ResourceUtils

class FileSystemResourceLoader(baseDir: File) extends ResourceLoader {

  def load(file: File) = {
    new FileSystemResource(baseDir, ResourceUtils.getRelativePath(file.getAbsolutePath, baseDir.getAbsolutePath, File.separator))
  }

  def load = {
    FileUtils.listFiles(baseDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE).map(load).toSeq
  }

}

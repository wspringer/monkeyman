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

import java.io.File
import org.apache.commons.io.FileUtils
import nl.flotsam.monkeyman.ext.ResourceUtils
import collection.JavaConversions._
import org.apache.commons.io.filefilter._
import net.contentobjects.jnotify.{JNotifyListener, JNotify}
import scala.util.control.Exception.catching
import collection.mutable.Buffer

class FileSystemResourceLoader(baseDir: File)
  extends ResourceLoader {

  private val listeners = Buffer.empty[ResourceListener]

  private val watchID: Option[Int] = catching(classOf[UnsatisfiedLinkError]).opt {
    JNotify.addWatch(baseDir.getAbsolutePath, JNotify.FILE_CREATED & JNotify.FILE_MODIFIED & JNotify.FILE_RENAMED, true, new JNotifyListener() {

      def fileCreated(wd: Int, rootPath: String, name: String) {
        listeners.map(_.added(new FileSystemResource(baseDir, name)))
      }

      def fileDeleted(wd: Int, rootPath: String, name: String) {
        listeners.map(_.deleted(name))
      }

      def fileModified(wd: Int, rootPath: String, name: String) {
        println("***** MODIFIED ****")
        listeners.map {
          listener =>
            listener.deleted(name)
            listener.added(new FileSystemResource(baseDir, name))
        }
      }

      def fileRenamed(wd: Int, rootPath: String, oldName: String, newName: String) {
        listeners.map {
          listener =>
            listener.deleted(oldName)
            listener.added(new FileSystemResource(baseDir, newName))
        }
      }
    })
  }

  def dispose {
    watchID.map(JNotify.removeWatch(_))
  }

  def load(file: File) = {
    new FileSystemResource(baseDir, ResourceUtils.getRelativePath(file.getAbsolutePath, baseDir.getAbsolutePath,
      File.separator))
  }

  def load = {
    FileUtils.listFiles(baseDir,
      new NotFileFilter(
        new OrFileFilter(
          List(
            new PrefixFileFilter(".#"),
            new SuffixFileFilter("~")
          )
        )
      ), TrueFileFilter.INSTANCE).map(load).toSeq
  }

  def register(listener: ResourceListener) {
    listeners += listener
  }

  def unregister(listener: ResourceListener) {
    listeners -= listener
  }
}

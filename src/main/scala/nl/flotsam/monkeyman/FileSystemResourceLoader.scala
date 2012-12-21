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

import nl.flotsam.monkeyman.ext.ResourceUtils
import org.apache.commons.io.FileUtils
import collection.mutable.Buffer
import name.pachler.nio.file.StandardWatchEventKind._
import java.io.{FileFilter, File}
import collection.JavaConversions
import JavaConversions._
import util.Logging
import name.pachler.nio.file._
import java.util.concurrent.Executors
import org.apache.commons.io.filefilter._
import scala.Some

class FileSystemResourceLoader(baseDir: File)
  extends ResourceLoader with Logging {

  def expand(dir: File): List[File] =
    (Option(dir.listFiles(new FileFilter() {
      def accept(subdir: File) = subdir.isDirectory
    })) match {
      case Some(subdirs) => dir :: subdirs.toList.flatMap(expand)
      case None => List(dir)
    })

  def relative(path: Path) =
    ResourceUtils.getRelativePath(path.toString, baseDir.getAbsolutePath, File.separator)

  private val listeners = Buffer.empty[ResourceListener]
  private val keys = collection.mutable.Map.empty[WatchKey, Path]
  private val watchService = FileSystems.getDefault.newWatchService()

  for {
    dir <- expand(baseDir)
    path = Paths.get(dir.getAbsolutePath)
  } {
    info("Registering " + path)
    keys += path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY) -> path
  }

  private val watchThread = Executors.newSingleThreadExecutor().submit(new Runnable() {
    def run() {
      while (true) {
        val watchKey: WatchKey = watchService.take()
        keys.get(watchKey) match {
          case Some(dir) =>
            val events = watchKey.pollEvents().toList
            for (event <- events) {
              val kind = event.kind()
              if (kind == ENTRY_CREATE) {
                val created = dir.resolve(event.context().asInstanceOf[Path])
                if ((new File(created.toString)).isDirectory) {
                  debug("Added directory {}", created)
                  keys += created.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY) -> created
                  listeners.map(_.added(new FileSystemResource(baseDir, relative(created))))
                } else {
                  debug("Added file {}", created)
                  listeners.map(_.added(new FileSystemResource(baseDir, relative(created))))
                }
              }
              else if (kind == ENTRY_DELETE) {
                val path = event.context().asInstanceOf[Path]
                val deleted = dir.resolve(path)
                debug("Deleted {}", deleted)
                listeners.map(_.deleted(relative(deleted)))
              }
              else if (kind == ENTRY_MODIFY) {
                val modified = dir.resolve(event.context().asInstanceOf[Path])
                if (!(new File(modified.toString)).isDirectory) {
                  debug("Modified {}", modified)
                  listeners.map(_.modified(new FileSystemResource(baseDir, relative(modified))))
                }
              } else warn("Got unexpected type " + kind)
            }
          case _ =>
            warn("Illegal state, time to restart")
        }
        watchKey.reset()
      }
    }
  })

  def dispose {
    for ((key, path) <- keys) {
      try { key.cancel() } catch { case t: Throwable => }
    }
    watchService.close()
    watchThread.cancel(true)
  }

  def load(file: File) = {
    val relative =
      if (file == baseDir) ""
      else ResourceUtils.getRelativePath(file.getAbsolutePath, baseDir.getAbsolutePath, File.separator)
    new FileSystemResource(baseDir, relative)
  }

  def load = {
    FileUtils.listFilesAndDirs(baseDir,
      new NotFileFilter(
        new OrFileFilter(
          List(
            new PrefixFileFilter(".#"),
            new SuffixFileFilter("~"),
            new PrefixFileFilter("."),
            new RegexFileFilter("^#[^#]*#$")
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

/*
 * Monkeyman static web site generator
 * Copyright (C) 2013  Wilfred Springer
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

import util.Closeables._
import org.apache.commons.io.IOUtils
import java.net.InetSocketAddress
import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import java.util.concurrent.Executors
import util.Logging
import org.clapper.argot.ArgotConverters._
import java.io.ByteArrayOutputStream


object MonkeymanServer extends MonkeymanTool("monkeyman server") with Logging {

  private val DEFAULT_PORT: Int = 4567

  private val port = parser.option[Int](List("p", "port"), "PORT",
    "The port on which the server will be listening. Defaults to " + DEFAULT_PORT + ".")

  private val browsing =  parser.flag(List("d", "directory-browsing"), "Directory browsing.")

  override def directoryBrowsing = browsing.value == Some(true)

  def execute(config: MonkeymanConfiguration) {
    val selectedPort = port.value.getOrElse(DEFAULT_PORT)
    val address = new InetSocketAddress(selectedPort)
    val server = HttpServer.create(address, 0)
    server.createContext("/", new MonkeymanHandler(config))
    server.setExecutor(Executors.newCachedThreadPool())
    server.start()
    info("The Monkeyman is standing watch on port {}", selectedPort)
    def doWait() {
      try {
        Thread.sleep(1000)
      } catch {
        case _: InterruptedException => ()
      }
      if (System.in.available() <= 0)
        doWait()
    }
    doWait()
    info("The Monkeyman is standing down")
    config.dispose
    server.stop(0)
  }

  class MonkeymanHandler(config: MonkeymanConfiguration) extends HttpHandler {
    def handle(exchange: HttpExchange) {
      val path = exchange.getRequestURI.getPath
      info("Handling {} for {}", exchange.getRequestMethod, path)
      if (exchange.getRequestMethod == "GET") {
        val lookup = path.substring(1)
        config.registry.resourceByPath.get(lookup) match {
          case Some(resource) if resource.contentType == "application/directory" =>
            val altPath =
              if (resource.path == "") "index.html"
              else resource.path + "/index.html"
            config.registry.resourceByPath.get(altPath) match {
              case Some(altResource) =>
                info("Sending index.html")
                sendResource(altResource, exchange)
              case None =>
                sendStatus(404, "Not found", exchange)
            }
          case Some(resource) =>
            sendResource(resource, exchange)
          case None =>
            sendStatus(404, "Not found", exchange)
        }
      } else {
        println(config.registry.allResources.map(_.path).mkString("\n"))
        sendStatus(405, "Method not allowed", exchange)
      }
    }
  }

  private def sendResource(resource: Resource, exchange: HttpExchange) {
    try {
      using(new ByteArrayOutputStream) {
        out =>
          using(resource.open) {
            in =>
              IOUtils.copy(in, out)
          }
          val buffer = out.toByteArray
          val responseHeaders = exchange.getResponseHeaders
          responseHeaders.set("Content-Type", resource.contentType)
          exchange.sendResponseHeaders(200, buffer.length)
          using(exchange.getResponseBody)(_.write(buffer))
      }
    } catch {
      case t: Throwable =>
        error("Failed to handle request", t)
        sendStatus(500, "Failed to handle request", exchange)
    }
  }

  private def sendStatus(status: Int, message: String, exchange: HttpExchange) {
    val responseHeaders = exchange.getResponseHeaders
    responseHeaders.set("Content-Type", "text/plain")
    exchange.sendResponseHeaders(status, message.getBytes("UTF-8").length)
    using(exchange.getResponseBody) {
      out =>
        out.write(message.getBytes("UTF-8"))
        out.flush()
    }
  }

}

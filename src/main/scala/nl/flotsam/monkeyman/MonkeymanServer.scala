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

import util.Closeables._
import org.apache.commons.io.IOUtils
import java.net.InetSocketAddress
import com.sun.net.httpserver.{HttpServer, HttpExchange, HttpHandler}
import java.util.concurrent.Executors
import util.Logging
import org.clapper.argot.ArgotConverters._


object MonkeymanServer extends MonkeymanTool("monkeyman server") with Logging {

  private val DEFAULT_PORT: Int = 4567

  private val port = parser.option[Int](List("p", "port"), "PORT",
    "The port on which the server will be listening. Defaults to " + DEFAULT_PORT + ".")
  
  def execute(config: MonkeymanConfiguration) {
    config.resourceLoader.load // Force all resources to be loaded
    val selectedPort = port.value.getOrElse(DEFAULT_PORT)
    val address = new InetSocketAddress(selectedPort)
    val server = HttpServer.create(address, 0)
    server.createContext("/", new MonkeymanHandler(config))
    server.setExecutor(Executors.newCachedThreadPool())
    server.start()
    info("The Monkeyman is standing watch on port {}", selectedPort)
    while (true) Thread.sleep(1000)
  }

  class MonkeymanHandler(config: MonkeymanConfiguration) extends HttpHandler {
    def handle(exchange: HttpExchange) {
      val path = exchange.getRequestURI.getPath
      info("Handling {} for {}", exchange.getRequestMethod, path)
      if (exchange.getRequestMethod == "GET") {
        val lookup =
          if (path == "/") "index.html"
          else path.substring(1)
        config.registryDecorator.resourceByPath.get(lookup) match {
          case Some(resource) =>
            val responseHeaders = exchange.getResponseHeaders
            responseHeaders.set("Content-Type", resource.contentType)
            exchange.sendResponseHeaders(200, 0)
            using(resource.open) {
              in =>
                using(exchange.getResponseBody) {
                  out =>
                    IOUtils.copy(in, out)
                }
            }
          case None =>
            exchange.sendResponseHeaders(404, 0)
        }
      } else {
        exchange.sendResponseHeaders(404, 0)
      }
    }
  }

}

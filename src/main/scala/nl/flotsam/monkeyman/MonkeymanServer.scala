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


object MonkeymanServer extends MonkeymanTool("monkeyman server") with Logging {

  def execute(config: MonkeymanConfiguration) {
    config.resourceLoader.load // Force all resources to be loaded
    val address = new InetSocketAddress(8090)
    val server = HttpServer.create(address, 0)
    server.createContext("/", new MonkeymanHandler(config))
    server.setExecutor(Executors.newCachedThreadPool())
    server.start()
    while (true) Thread.sleep(1000)
  }

  class MonkeymanHandler(config: MonkeymanConfiguration) extends HttpHandler {
    def handle(exchange: HttpExchange) {
      val path = exchange.getRequestURI.getPath
      if (exchange.getRequestMethod == "GET")
        info("Handling request for {}", path)
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
    }
  }

}

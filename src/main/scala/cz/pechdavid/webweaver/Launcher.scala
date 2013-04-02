package cz.pechdavid.webweaver

import crawler.{PersistentQueue, WebWeaver}
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.extension.http.WebServerModule
import fts.FulltextProjection
import spray.routing.{Directives, Route}
import akka.actor.Props
import web.Controller
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams

/**
 * Created: 2/22/13 5:46 PM
 */
object Launcher extends App with Directives {

  // FIXME: config
  val conn = ConnectionParams("localhost", "mycelium")

  // FIXME: config
  val httpServer = (ms: ModuleSpec) => {
    Props(new WebServerModule(ms.name, "localhost", 8080)(Controller))
  }

  val ftsModules = (ms: ModuleSpec) => {
    Props(new FulltextProjection)
  }

  val queue = (ms: ModuleSpec) => {
    Props(new PersistentQueue(conn))
  }

  new WebWeaver(Map("httpServer" -> httpServer, "fulltextProjection" -> ftsModules, "queue" -> queue),
    List(ModuleSpec("fulltextProjection"), ModuleSpec("httpServer"), ModuleSpec("queue"))
  )

}

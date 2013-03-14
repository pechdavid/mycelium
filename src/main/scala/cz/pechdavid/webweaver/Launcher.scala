package cz.pechdavid.webweaver

import crawler.WebWeaver
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.extension.http.WebServerModule
import fts.FulltextProjection
import spray.routing.{Directives, Route}
import akka.actor.Props
import web.Controller

/**
 * Created: 2/22/13 5:46 PM
 */
object Launcher extends App with Directives {



  val httpServer = (ms: ModuleSpec) => {
    Props(new WebServerModule(ms.name, "localhost", 8080)(Controller))
  }

  val ftsModules = (ms: ModuleSpec) => {
    Props(new FulltextProjection)
  }

  new WebWeaver(Map("httpServer" -> httpServer, "fulltextProjection" -> ftsModules),
    List(ModuleSpec("fulltextProjection"), ModuleSpec("httpServer"))
  )

}

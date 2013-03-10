package cz.pechdavid.webweaver

import crawler.WebWeaver
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.extension.http.WebServerModule
import spray.routing.{Directives, Route}
import akka.actor.Props

/**
 * Created: 2/22/13 5:46 PM
 */
object Launcher extends App with Directives {

  val route: Route = {
    path("") {
      get {
        complete {
          "Hello!"
        }
      }
    }
  }


  val httpServer = (ms: ModuleSpec) => {
    Props(new WebServerModule(ms.name, "localhost", 8080)(route))
  }

  new WebWeaver(Map("httpServer" -> httpServer),
    List(ModuleSpec("httpServer"))
  )

}

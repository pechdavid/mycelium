package cz.pechdavid.webweaver

import cz.pechdavid.webweaver.crawler.{RequeueProjection, DownloadHandler, PersistentQueue, WebWeaver}
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.extension.http.WebServerModule
import fts.FulltextProjection
import spray.routing.Directives
import akka.actor.Props
import web.Controller
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.structured.{StructuredContentProjection, ParserEventHandler}
import cz.pechdavid.webweaver.raw.RawContentProjection

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

  val requeue = (ms: ModuleSpec) => {
    Props(new RequeueProjection)
  }

  val structured = (ms: ModuleSpec) => {
    Props(new StructuredContentProjection(conn))
  }

  /*
   *  FIXME: gzip event handler..
   */
  val rawContent = (ms: ModuleSpec) => {
    // FIXME: CONN
    Props(new RawContentProjection())
  }


  new WebWeaver(Map("httpServer" -> httpServer, "fulltextProjection" -> ftsModules,
      "queue" -> queue, "requeueProjection" -> requeue, "structuredContentProjection" -> structured),
    List(ModuleSpec("fulltextProjection"), ModuleSpec("httpServer"), ModuleSpec("queue"), ModuleSpec("requeueProjection"),
      ModuleSpec("structuredContentProjection")),
    List(new DownloadHandler),
    List(new ParserEventHandler(Set("requeueProjection", "structuredContentProjection", "fulltextProjection")))
  )

}

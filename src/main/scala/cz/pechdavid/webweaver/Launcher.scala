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
import cz.pechdavid.webweaver.raw.{GzipEventHandler, RawContentProjection}
import cz.pechdavid.webweaver.graph.GraphProjection

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
    Props(new RequeueProjection(Option(Set("raynet.cz", "simlog.cz"))))
  }

  val structured = (ms: ModuleSpec) => {
    Props(new StructuredContentProjection(conn))
  }

  val rawContent = (ms: ModuleSpec) => {
    Props(new RawContentProjection(conn))
  }

  val graphProjection = (ms: ModuleSpec) => {
    Props(new GraphProjection)
  }

  new WebWeaver(Map("httpServer" -> httpServer, "fulltextProjection" -> ftsModules,
      "queue" -> queue, "requeueProjection" -> requeue, "structuredContentProjection" -> structured,
      "rawContentProjection" -> rawContent, "graphProjection" -> graphProjection),
    List(ModuleSpec("fulltextProjection"), ModuleSpec("httpServer"), ModuleSpec("queue"), ModuleSpec("requeueProjection"),
      ModuleSpec("structuredContentProjection"), ModuleSpec("rawContentProjection"), ModuleSpec("graphProjection")),
    List(new DownloadHandler),
    List(new ParserEventHandler(Set("requeueProjection", "structuredContentProjection", "fulltextProjection", "graphProjection")),
      new GzipEventHandler(Set("rawContentProjection")))
  )

}

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
import cz.pechdavid.webweaver.stats.{DomainProjection, DownloadUseCase, ResendProjection}
import cz.pechdavid.mycelium.core.domain.RelayEventHandler

/**
 * Created: 2/22/13 5:46 PM
 */

object Launcher extends App with Directives {


  val conn = ConnectionParams("localhost", "mycelium")

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
    Props(new RequeueProjection(Option(Set("raynet.cz", "www.raynetmarketing.cz", "www.simlog.cz", "cs.wikipedia.org"))))
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

  val resendProjection = (ms: ModuleSpec) => {
    Props(new ResendProjection)
  }

  val domainProjection = (ms: ModuleSpec) => {
    Props(new DomainProjection)
  }

  new WebWeaver(Map("httpServer" -> httpServer, "fulltextProjection" -> ftsModules,
      "queue" -> queue, "requeueProjection" -> requeue, "structuredContentProjection" -> structured,
      "rawContentProjection" -> rawContent, "graphProjection" -> graphProjection, "resendProjection" -> resendProjection,
      "domainProjection" -> domainProjection),
    List(ModuleSpec("fulltextProjection"), ModuleSpec("httpServer"), ModuleSpec("queue"), ModuleSpec("requeueProjection"),
      ModuleSpec("structuredContentProjection"), ModuleSpec("rawContentProjection"), ModuleSpec("graphProjection"),
      ModuleSpec("resendProjection"), ModuleSpec("domainProjection")),
    List(new DownloadHandler, new DownloadUseCase),
    List(new ParserEventHandler(Set("requeueProjection", "structuredContentProjection", "fulltextProjection", "graphProjection",
        "resendProjection")), new RelayEventHandler(Set("domainProjection")),
      new GzipEventHandler(Set("rawContentProjection")))
  )

}

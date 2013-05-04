package cz.pechdavid.webweaver.web

import spray.routing.Directives
import cz.pechdavid.webweaver.raw.RawContentTrl
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.structured.StructuredContentTrl
import cz.pechdavid.webweaver.graph.{TreeNode, GraphTrl}
import akka.actor.{Actor, Props, ActorRefFactory, ActorRef}
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine}
import java.io.{PrintWriter, StringWriter, File}
import spray.http.MediaTypes
import cz.pechdavid.mycelium.extension.http.RoutingRules
import cz.pechdavid.webweaver.fts.{FulltextSearch, FulltextResult, FulltextRecent}
import akka.pattern._
import concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout
import cz.pechdavid.webweaver.crawler.{QueuePeek, AddToQueue}
import net.liftweb.json.{JsonAST, Printer, Extraction}
import cz.pechdavid.webweaver.stats.DomainTrl
import dispatch.url

/**
 * Created: 3/10/13 1:31 PM
 */

case class SearchQuery(query: Option[String])

case class ToQueue(to_queue: Option[String])

case class ControllerGraph(url: Option[String])

case class ControllerGraphJson(url: Option[String])

case object ControllerStats

case class ControllerStructured(url: Option[String])

case class UrlWrapper(url: Option[String])

case class ControllerRaw(url: Option[String])

case class ControllerDownload(url: Option[String])

case class ControllerIndex(queue: Option[String])

object Controller extends RoutingRules with Directives {

  def routing(ftsModule: ActorRef, queue: ActorRef)(implicit actorRefFactory: ActorRefFactory) = {
    val con = ConnectionParams("localhost", "mycelium")
    val controller = actorRefFactory.actorOf(Props(new Controller(con, con, con, con, ftsModule, queue)), "controller")
    implicit val timeout = Timeout(1 minute)

    path("") {
      get {
        redirect("index.html")
      }
    } ~
      path("graph.json") {
        get {
          parameters('url ?).as(UrlWrapper) {
            url: UrlWrapper =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? ControllerGraphJson(url.url), 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      path("graph.html") {
        get {
          parameters('url ?).as(UrlWrapper) {
            url: UrlWrapper =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? ControllerGraph(url.url), 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      path("search.html") {
        get {
          parameters('query ?).as(SearchQuery) {
            query =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? query, 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      path("stats.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              Await.result(controller ? ControllerStats, 1 minute).asInstanceOf[String]
            }
          }
        }
      } ~
      path("structured.html") {
        get {
          parameters('url ?).as(UrlWrapper) {
            url: UrlWrapper =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? ControllerStructured(url.url), 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      path("raw.html") {
        get {
          parameters('url ?).as(UrlWrapper) {
            url: UrlWrapper =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? ControllerRaw(url.url), 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      path("download.html") {
        get {
          parameters('url ?).as(UrlWrapper) {
            url: UrlWrapper =>
              complete {
                Await.result(controller ? ControllerDownload(url.url), 1 minute).asInstanceOf[String]
              }
          }
        }
      } ~
      path("index.html") {
        get {
          parameters('to_queue ?).as(ToQueue) {
            toQueue: ToQueue =>
              respondWithMediaType(MediaTypes.`text/html`) {
                complete {
                  Await.result(controller ? ControllerIndex(toQueue.to_queue), 1 minute).asInstanceOf[String]
                }
              }
          }
        }
      } ~
      pathPrefix("js") {
        getFromResourceDirectory("js")
      } ~
      pathPrefix("css") {
        getFromResourceDirectory("css")
      } ~
      pathPrefix("img") {
        getFromResourceDirectory("img")
      }
  }


  val tplEngine = {
    val tplEngine = new TemplateEngine(Set(new File("/"), new File("/WEB-INF/scalate"), new File("/WEB-INF/scalate/layouts"),
      new File("/WEB-INF"), new File("src/main/webapp/WEB-INF"), new File("src/main/webapp/WEB-INF/scalate/layouts"),
      new File("src/main/webapp"), new File("src/main/webapp/WEB-INF/scalate")))
    tplEngine.boot
    tplEngine
  }
}


class Controller(rawCon: ConnectionParams, structuredCon: ConnectionParams, graphCon: ConnectionParams,
                 statsCon: ConnectionParams, ftsModule: ActorRef, queueWorker: ActorRef) extends Actor {

  val rawTrl = new RawContentTrl(rawCon)
  val structuredTrl = new StructuredContentTrl(structuredCon)
  val graphTrl = new GraphTrl(graphCon)
  val domainTrl = new DomainTrl

  implicit val timeout = Timeout(1 minute)

  private def render(tpl: String, map: Map[String, Any] = Map.empty) = {
    val writer = new StringWriter()
    val renderContext = new DefaultRenderContext(tpl, Controller.tplEngine, new PrintWriter(writer))

    val updatedMap = map ++ Map("page" -> tpl.replaceAll("\\.ssp$", ""))

    renderContext.layout("default.ssp", updatedMap) {
      renderContext.render(tpl, updatedMap)
    }

    writer.toString
  }

  private def recent = {
    val res = Await.result((ftsModule ? FulltextRecent).mapTo[Array[FulltextResult]], 1 minute)

    "recent" -> res
  }

  private def renderJson(node: TreeNode) = {
    implicit val formats = net.liftweb.json.DefaultFormats
    Printer.pretty(JsonAST.render(Extraction.decompose(node)))
  }

  def receive = {
    case req: ControllerIndex =>

      var valid = true
      if (req.queue.isDefined) {
        valid = AddToQueue.isValid(req.queue.get)

        if (valid) {
          queueWorker ! AddToQueue(req.queue.get)
        }
      }
      val q = Await.result((queueWorker ? QueuePeek).mapTo[Iterator[String]], 1 minute)

      sender ! render("index.ssp", Map(recent, "valid" -> valid, "queue" -> q))

    case query: SearchQuery =>
      val q = query.query match {
        case Some(que) =>
          Await.result((ftsModule ? FulltextSearch(que)).mapTo[Array[FulltextResult]], 1 minute)
        case None =>
          Array.empty[FulltextResult]
      }

      sender ! render("search.ssp", Map(recent, "query" -> query, "results" -> q))

    case req: ControllerGraph =>
      sender ! render("graph.ssp", Map(recent, "url" -> req.url))

    case req: ControllerGraphJson =>
      req.url match {
        case Some(u) =>
          sender ! renderJson(graphTrl.treeFromUrl(u))

        case None =>
          sender ! "URL not indexed"
      }

    case raw: ControllerRaw =>
      sender ! render("raw.ssp", Map(recent, "url" -> raw.url))

    case dwn: ControllerDownload =>
      dwn.url match {
        case Some(url) =>
          rawTrl.byUrl(url) match {
            case Some(cont) =>
              sender ! cont
            case None =>
              sender ! "File not found."
          }
        case None =>
          sender ! "File not found."
      }

    case req: ControllerStructured =>

      val doc = req.url match {
        case Some(u) =>
          structuredTrl.byUrl(u)
        case None =>
          None
      }

      sender ! render("structured.ssp", Map(recent, "url" -> req.url, "doc" -> doc))

    case ControllerStats =>
      val domains = domainTrl.statRandomDomains
      sender ! render("stats.ssp", Map(recent, "domains" -> domains))
  }
}

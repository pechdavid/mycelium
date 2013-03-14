package cz.pechdavid.webweaver.web

import spray.routing.Directives
import cz.pechdavid.webweaver.raw.RawContentTrl
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.structured.StructuredContentTrl
import cz.pechdavid.webweaver.graph.GraphTrl
import akka.actor.{Actor, Props, ActorRefFactory, ActorRef}
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine}
import java.io.{PrintWriter, StringWriter, File}
import spray.http.MediaTypes
import cz.pechdavid.mycelium.extension.http.RoutingRules
import cz.pechdavid.webweaver.fts.{FulltextResult, FulltextRecent}
import akka.pattern._
import concurrent.Await
import scala.concurrent.duration._
import akka.util.Timeout

/**
 * Created: 3/10/13 1:31 PM
 */

case class SearchQuery(query: Option[String])

case object ControllerGraph
case object ControllerStats
case object ControllerStructured
case object ControllerRaw
case object ControllerIndex

object Controller extends RoutingRules with Directives {

  def routing(ftsModule: ActorRef)(implicit actorRefFactory: ActorRefFactory) = {
    val con = ConnectionParams("localhost", "mycelium")
    val controller = actorRefFactory.actorOf(Props(new Controller(con, con, con, con, ftsModule)), "controller")
    implicit val timeout = Timeout(1 minute)

    path("") {
      get {
        redirect("index.html")
      }
    } ~
      path("graph.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              Await.result(controller ? ControllerGraph, 1 minute).asInstanceOf[String]
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
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              Await.result(controller ? ControllerStructured, 1 minute).asInstanceOf[String]
            }
          }
        }
      } ~
      path("raw.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              Await.result(controller ? ControllerRaw, 1 minute).asInstanceOf[String]
            }
          }
        }
      } ~
      path("index.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              Await.result(controller ? ControllerIndex, 1 minute).asInstanceOf[String]
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


}

class Controller(rawCon: ConnectionParams, structuredCon: ConnectionParams, graphCon: ConnectionParams,
                 statsCon: ConnectionParams, ftsModule: ActorRef) extends Actor {


  val tplEngine = new TemplateEngine(Set(new File("/"), new File("/WEB-INF/scalate"), new File("/WEB-INF/scalate/layouts"),
    new File("/WEB-INF"), new File("src/main/webapp/WEB-INF"), new File("src/main/webapp/WEB-INF/scalate/layouts"),
    new File("src/main/webapp"), new File("src/main/webapp/WEB-INF/scalate")))
  tplEngine.boot

  val rawTrl = new RawContentTrl(rawCon)
  val structuredTrl = new StructuredContentTrl(structuredCon)
  val graphTrl = new GraphTrl(graphCon)
  //val statsTrl = new StatsTrl(statsCon)

  implicit val timeout = Timeout(1 minute)

  private def render(tpl: String, map: Map[String, Any] = Map.empty) = {
    val writer = new StringWriter()
    val renderContext = new DefaultRenderContext("index.html", tplEngine, new PrintWriter(writer))

    val updatedMap = map ++ Map("page" -> tpl.replaceAll("\\.ssp$", ""))

    renderContext.layout("default.ssp", updatedMap) {
      renderContext.render(tpl, updatedMap)
    }

    writer.toString
  }

  def receive = {
    case ControllerIndex =>

      val res = Await.result((ftsModule ? FulltextRecent).mapTo[Array[FulltextResult]], 1 minute)

      sender ! render("index.ssp", Map("recent" -> res))

    case query: SearchQuery =>
      //implicit val timeout = 1 minute
      //val lst = Await.result(actorRefFactory.actorFor(ModuleRef.ModulePathPrefix + "fulltext") ? FulltextSearch(query.query),
      //  1 minute).asInstanceOf[List[FulltextResult]]

      // FIXME: RECENT!

      sender ! render("search.ssp", Map("query" -> query))

    case ControllerGraph =>
      sender ! render("graph.ssp")

    case ControllerRaw =>
      sender ! render("raw.ssp")

    case ControllerStructured =>
      sender ! render("structured.ssp")

    case ControllerStats =>
      sender ! render("stats.ssp")
  }
}

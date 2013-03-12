package cz.pechdavid.webweaver.web

import spray.routing.Directives
import cz.pechdavid.webweaver.raw.RawContentTrl
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.structured.StructuredContentTrl
import cz.pechdavid.webweaver.graph.GraphTrl
import akka.actor.{ActorRefFactory, ActorRef}
import org.fusesource.scalate.{DefaultRenderContext, TemplateEngine}
import java.io.{PrintWriter, StringWriter, File}
import spray.http.MediaTypes
import cz.pechdavid.mycelium.extension.http.RoutingRules

/**
 * Created: 3/10/13 1:31 PM
 */

case class SearchQuery(query: Option[String])

object Controller extends RoutingRules with Directives {

  def routing(implicit actorRefFactory: ActorRefFactory) = {

    val con = ConnectionParams("localhost", "mycelium")

    val controller = new Controller(con, con, con, con, null)
    path("") {
      get {
        redirect("index.html")
      }
    } ~
      path("graph.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              controller.graph
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
                  controller.search(query)
                }
              }
          }
        }
      } ~
      path("stats.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              controller.stats
            }
          }
        }
      } ~
      path("structured.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              controller.structured
            }
          }
        }
      } ~
      path("raw.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              controller.raw
            }
          }
        }
      } ~
      path("index.html") {
        get {
          respondWithMediaType(MediaTypes.`text/html`) {
            complete {
              controller.index
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
                 statsCon: ConnectionParams, ftsModule: ActorRef) {


  val tplEngine = new TemplateEngine(Set(new File("/"), new File("/WEB-INF/scalate"), new File("/WEB-INF/scalate/layouts"),
    new File("/WEB-INF"), new File("src/main/webapp/WEB-INF"), new File("src/main/webapp/WEB-INF/scalate/layouts"),
    new File("src/main/webapp"), new File("src/main/webapp/WEB-INF/scalate")))
  tplEngine.boot

  val rawTrl = new RawContentTrl(rawCon)
  val structuredTrl = new StructuredContentTrl(structuredCon)
  val graphTrl = new GraphTrl(graphCon)
  //val statsTrl = new StatsTrl(statsCon)

  private def render(tpl: String, map: Map[String, Any] = Map.empty) = {
    val writer = new StringWriter()
    val renderContext = new DefaultRenderContext("index.html", tplEngine, new PrintWriter(writer))

    val updatedMap = map ++ Map("page" -> tpl.replaceAll("\\.ssp$", ""))

    renderContext.layout("default.ssp", updatedMap) {
      renderContext.render(tpl, updatedMap)
    }

    writer.toString
  }

  def index = {
    render("index.ssp")
  }

  def search(query: SearchQuery)(implicit actorRefFactory: ActorRefFactory) = {
    //implicit val timeout = 1 minute
    //val lst = Await.result(actorRefFactory.actorFor(ModuleRef.ModulePathPrefix + "fulltext") ? FulltextSearch(query.query),
    //  1 minute).asInstanceOf[List[FulltextResult]]

    // FIXME: RECENT!

    render("search.ssp", Map("query" -> query))
  }

  def graph = {
    render("graph.ssp")
  }

  def raw = {
    render("raw.ssp")
  }

  def structured = {
    render("structured.ssp")
  }

  def stats = {
    render("stats.ssp")
  }
}

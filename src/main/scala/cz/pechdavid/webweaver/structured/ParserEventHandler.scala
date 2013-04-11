package cz.pechdavid.webweaver.structured

import cz.pechdavid.mycelium.core.event.EventHandler
import cz.pechdavid.webweaver.crawler.Downloaded
import org.jsoup.Jsoup
import cz.pechdavid.mycelium.core.projection.NotifyProjections

import scala.collection.JavaConversions._
import java.net.URL

/**
 * Created: 2/23/13 8:32 PM
 */
class ParserEventHandler(notify: Set[String]) extends EventHandler {
  def handle = {
    case Downloaded(url, cont) =>
      val doc = Jsoup.parse(cont, url)

      val u = new URL(url)

      val refs = doc.getElementsByTag("a").map {
        _.attr("href")
      }.map {
        _.replaceFirst("#.*$", "")
      }.filter {
        !_.isEmpty
      }.map {
        new URL(u, _).toString
      }.toSet

      List(NotifyProjections(notify, ParsedHtml(url, doc.title(), refs)))
  }
}

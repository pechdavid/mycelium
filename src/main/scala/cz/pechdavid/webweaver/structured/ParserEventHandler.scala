package cz.pechdavid.webweaver.structured

import cz.pechdavid.mycelium.core.event.EventHandler
import cz.pechdavid.webweaver.crawler.Downloaded
import org.jsoup.Jsoup
import cz.pechdavid.mycelium.core.projection.NotifyProjections

import scala.collection.JavaConversions._
import java.net.URL
import org.jsoup.nodes.Document

/**
 * Created: 2/23/13 8:32 PM
 */
class ParserEventHandler(notify: Set[String]) extends EventHandler {

  private def refs(u: URL, doc: Document) = {
    doc.getElementsByTag("a").map {
        _.attr("href")
      }.map {
        _.replaceFirst("#.*$", "")
      }.filter {
        !_.isEmpty
      }.map {
        new URL(u, _).toString
      }.toSet
  }

  private def images(u: URL, doc: Document) = {
    doc.getElementsByTag("img").map {
      _.attr("src")
    }.map {
      new URL(u, _).toString
    }.toSet
  }

  def handle = {
    case Downloaded(url, cont) =>
      val doc = Jsoup.parse(cont, url)
      val u = new URL(url)

      List(NotifyProjections(notify, ParsedHtml(url, doc.title(), refs(u, doc), images(u, doc))))
  }
}

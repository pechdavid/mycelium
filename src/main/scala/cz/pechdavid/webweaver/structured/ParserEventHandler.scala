package cz.pechdavid.webweaver.structured

import cz.pechdavid.mycelium.core.event.EventHandler
import cz.pechdavid.webweaver.crawler.Downloaded
import org.jsoup.Jsoup
import cz.pechdavid.mycelium.core.projection.NotifyProjections

/**
 * Created: 2/23/13 8:32 PM
 */
class ParserEventHandler(notify: Set[String]) extends EventHandler {
  def handle = {
    case Downloaded(url, cont) =>
      val doc = Jsoup.parse(cont, url)

      List(NotifyProjections(notify, ParsedHtml(url, doc.title())))
  }
}

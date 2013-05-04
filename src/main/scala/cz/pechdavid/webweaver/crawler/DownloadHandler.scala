package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.command.CommandHandler
import akka.event.slf4j.SLF4JLogging
import dispatch._

/**
 * Created: 2/22/13 7:49 PM
 */
class DownloadHandler extends CommandHandler with SLF4JLogging {
  def handle = {
    case DownloadUrl(u) =>
      log.debug("Download: " + url)

      val src = Http(url(u) OK as.String)

      val cont = src()

      log.debug("Done downloading: " + url)

      List(Downloaded(u, cont))
  }
}

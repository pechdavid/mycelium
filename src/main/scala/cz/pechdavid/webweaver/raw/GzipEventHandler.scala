package cz.pechdavid.webweaver.raw

import cz.pechdavid.mycelium.core.event.EventHandler
import cz.pechdavid.webweaver.crawler.Downloaded
import cz.pechdavid.mycelium.core.projection.NotifyProjections
import java.util.zip.GZIPOutputStream
import java.io.ByteArrayOutputStream

/**
 * Created: 2/24/13 5:48 PM
 */
class GzipEventHandler(notify: Set[String]) extends EventHandler {
  def handle = {
    case Downloaded(url, cont) =>
      val outStream = new ByteArrayOutputStream()
      val gzipOS = new GZIPOutputStream(outStream)

      val contBytes = cont.getBytes("utf-8")
      gzipOS.write(contBytes)
      gzipOS.flush()
      outStream.flush()
      gzipOS.close()

      val res = outStream.toByteArray

      List(NotifyProjections(notify, GzipRawContent(url, res)))
  }
}

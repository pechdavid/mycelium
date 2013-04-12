package cz.pechdavid.webweaver.raw

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/24/13 5:48 PM
 */
class RawContentProjection(conn: ConnectionParams) extends WorkerModule("rawContentProjection") with SLF4JLogging {

  val gridfs = conn.gridfs

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[GzipRawContent]
  }

  def handle = {
    case raw: GzipRawContent =>
      val file = gridfs.createFile(raw.content)
      file.filename = raw.url
      file.save()

      log.info("Saved file: {}, length: {}", raw.url, file.length)
  }
}

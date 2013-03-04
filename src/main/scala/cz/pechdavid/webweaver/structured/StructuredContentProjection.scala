package cz.pechdavid.webweaver.structured

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject
import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 2/24/13 5:48 PM
 */
class StructuredContentProjection(server: String, dbName: String) extends WorkerModule("structuredContentProjection") {
  val connection = MongoClient(server)
  val db = connection(dbName)
  val col = db(StructuredContentTrl.Collection)

  def handle = {
    case doc: ParsedHtml =>
      col += MongoDBObject("url" -> doc.url, "title" -> doc.title)
  }

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[ParsedHtml]
  }
}

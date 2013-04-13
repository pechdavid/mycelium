package cz.pechdavid.webweaver.structured

import com.mongodb.casbah.commons.{MongoDBListBuilder, MongoDBList, MongoDBObject}
import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams

/**
 * Created: 2/24/13 5:48 PM
 */
class StructuredContentProjection(connection: ConnectionParams) extends WorkerModule("structuredContentProjection") {
  val col = connection.collection(StructuredContentTrl.Collection)

  def handle = {
    case doc: ParsedHtml =>
      col += MongoDBObject("url" -> doc.url, "title" -> doc.title, "links" -> doc.links, "images" -> doc.images)
  }

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[ParsedHtml]
  }
}

package cz.pechdavid.webweaver.structured

import akka.actor.Actor
import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.commons.MongoDBObject

/**
 * Created: 2/24/13 5:48 PM
 */
class StructuredContentProjection(server: String, dbName: String) extends Actor {
  val connection = MongoClient(server)
  val db = connection(dbName)
  val col = db(StructuredContentTrl.Collection)

  def receive = {
    case doc: ParsedHtml =>
      col += MongoDBObject("url" -> doc.url, "title" -> doc.title)
  }
}

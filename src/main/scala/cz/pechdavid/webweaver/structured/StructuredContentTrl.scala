package cz.pechdavid.webweaver.structured

import com.mongodb.casbah.Imports._

/**
 * Created: 2/24/13 7:26 PM
 */
object StructuredContentTrl {
  val Collection = "structured"
}


class StructuredContentTrl(server: String, dbName: String) {
  val connection = MongoClient(server)
  val db = connection(dbName)
  val col = db(StructuredContentTrl.Collection)

  def byUrl(url: String) = {
    col.findOne(MongoDBObject("url" -> url))
  }
}

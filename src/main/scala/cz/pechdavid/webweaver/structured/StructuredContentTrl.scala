package cz.pechdavid.webweaver.structured

import com.mongodb.casbah.Imports._
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams

/**
 * Created: 2/24/13 7:26 PM
 */
object StructuredContentTrl {
  val Collection = "structured"
}


class StructuredContentTrl(connection: ConnectionParams) {
  val col = connection.collection(StructuredContentTrl.Collection)

  def byUrl(url: String) = {
    col.findOne(MongoDBObject("url" -> url))
  }
}

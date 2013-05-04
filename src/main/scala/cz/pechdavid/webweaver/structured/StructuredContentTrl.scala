/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.structured

import com.mongodb.casbah.Imports._
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams

/**
 * Reading structured document
 *
 * Created: 2/24/13 7:26 PM
 */
object StructuredContentTrl {
  val Collection = "structured"
}


class StructuredContentTrl(connection: ConnectionParams) {
  val col = connection.collection(StructuredContentTrl.Collection)

  def byUrl(url: String) = {
    col.findOne(MongoDBObject("url" -> url)) match {
      case Some(dbo) =>
        Option(ParsedHtml(dbo.as[String]("url"), dbo.as[String]("title"), dbo.as[MongoDBList]("links").toSet.map {
          el: Any =>
            el.asInstanceOf[String]
        }, dbo.as[MongoDBList]("images").toSet.map {
          el: Any =>
            el.asInstanceOf[String]
        }))
      case None =>
        None
    }
  }
}

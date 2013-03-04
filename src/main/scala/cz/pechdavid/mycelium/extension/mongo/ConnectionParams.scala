package cz.pechdavid.mycelium.extension.mongo

import com.mongodb.casbah.MongoClient
import com.mongodb.casbah.gridfs.Imports._

/**
 * Created: 3/4/13 11:31 PM
 */
case class ConnectionParams(server: String, dbName: String) {

  def connection = {
    MongoClient(server)
  }

  def db = {
    connection(dbName)
  }

  def collection(col: String) = {
    db(col)
  }

  def gridfs = {
    GridFS(db)
  }
}

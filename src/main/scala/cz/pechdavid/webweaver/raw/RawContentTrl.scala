package cz.pechdavid.webweaver.raw

import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import com.mongodb.casbah.gridfs.GridFSDBFile


/**
 * Created: 3/4/13 11:30 PM
 */
class RawContentTrl(con: ConnectionParams) {

  def byUrl(url: String): Option[GridFSDBFile] = ???

}

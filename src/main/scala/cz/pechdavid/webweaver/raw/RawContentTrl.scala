/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.raw

import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import com.mongodb.casbah.gridfs.GridFSDBFile
import java.util.zip.GZIPInputStream
import scala.io.Source


/**
 * Reads raw gzipped content from gridfs
 *
 * Created: 3/4/13 11:30 PM
 */
class RawContentTrl(con: ConnectionParams) {

  val gridfs = con.gridfs

  def byUrl(url: String): Option[String] = {
    gridfs.findOne(url) match {
      case Some(file) =>
        val gzip = new GZIPInputStream(file.inputStream)
        if (gzip.available() > 0) {
          val source = Source.fromInputStream(gzip)

          Option(source.getLines().mkString("\n"))
        } else {
          None
        }

      case None =>
        None
    }
  }
}

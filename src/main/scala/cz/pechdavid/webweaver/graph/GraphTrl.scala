package cz.pechdavid.webweaver.graph

import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import org.neo4j.graphdb.traversal.Traverser
import scala.collection.mutable
import scala.collection.JavaConversions._

/**
 * Created: 3/8/13 9:21 PM
 */
class GraphTrl(con: ConnectionParams) {
  def treeFromUrl(s: String): TreeNode = {
    Neo4jDb.treeFromUrl(s)
  }
}

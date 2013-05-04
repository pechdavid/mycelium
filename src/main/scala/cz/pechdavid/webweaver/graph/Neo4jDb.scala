/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.graph

import org.neo4j.scala.{SingletonEmbeddedGraphDatabaseServiceProvider, Neo4jWrapper}
import scala.sys.ShutdownHookThread
import org.neo4j.kernel.Traversal
import org.neo4j.graphdb.{Node, Direction}
import scala.collection.JavaConversions._
import org.neo4j.graphdb.traversal.{Evaluators, Traverser}
import scala.collection.mutable


case class StoredNode(name: String)

/**
 * In-JVM neo4j embedded storage
 *
 * Created: 4/13/13 12:00 PM
 */
object Neo4jDb extends Neo4jWrapper with SingletonEmbeddedGraphDatabaseServiceProvider {
  def neo4jStoreDir = "/tmp/webweaverneo4j"

  ShutdownHookThread {
    shutdown(ds)
  }

  def findOrCreate(name: String): Node = {
    val nodes = Traversal.traversal()
      .breadthFirst()
      .relationships("ROOT", Direction.OUTGOING)
      .evaluator(Evaluators.excludeStartPosition())
      .traverse(getReferenceNode(ds)).nodes()

    nodes.find {
      n =>
        n.hasProperty("name") && n.getProperty("name") == name

    } match {
      case Some(n) =>
        n
      case None =>
        val newNode = createNode(ds)

        newNode.setProperty("name", name)
        getReferenceNode(ds) --> "ROOT" --> newNode

        newNode
    }
  }

  def interconnect(left: Node, right: Node) {
    val alreadyConnected = left.getRelationships(Direction.OUTGOING, "LINKS").exists {
      rel =>
        rel.getEndNode == right
    }

    if (!alreadyConnected) {
      left --> "LINKS" --> right
    }
  }

  def maybeNodeAndLinks(base: String, links: Set[String]) {
    withTx {
      implicit neo =>
        val baseNode = findOrCreate(base)

        val linkNodes = links.map {
          findOrCreate(_)
        }

        for (link <- linkNodes) {
          interconnect(baseNode, link)
        }
    }
  }

  def treeFromUrl(base: String) = {
    translate(base,
      withTx {
        implicit neo =>
          val start = findOrCreate(base)
          Traversal.
            traversal().breadthFirst()
            .relationships("LINKS", Direction.OUTGOING)
            .evaluator(Evaluators.includingDepths(1, 2))
            .traverse(start)
      }
    )
  }

  private def translate(base: String, traverser: Traverser) = {
    val ret = TreeNode(toNode(base))

    for (each <- traverser.iterator().toList
      if each.endNode().hasProperty("name")) {

      var currentNode = ret

      for (rel <- each.relationships
        if currentNode.children.length < 80) {
        val label = toNode(rel.getEndNode.getProperty("name").asInstanceOf[String])

        val newNode = currentNode.children.find {
          _.name == label
        } match {
          case Some(innerNode) =>
            innerNode
          case None =>
            TreeNode(label)
        }

        currentNode.children += newNode
        currentNode = newNode
      }
    }

    ret
  }

  private def toNode(nodeName: String) = {
    nodeName.replaceFirst("http://", "").replaceFirst("https://", "")
  }

}

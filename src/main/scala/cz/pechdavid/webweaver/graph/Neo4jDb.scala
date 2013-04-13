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
            traversal().depthFirst()
            .relationships("LINKS", Direction.OUTGOING)
            .evaluator(Evaluators.includingDepths(1, 3))
            .traverse(start)
      }
    )
  }

  private def translate(base: String, traverser: Traverser) = {
    val stack = new mutable.Stack[TreeNode]
    stack.push(toNode(base))

    for (each <- traverser.iterator().toList
      if each.endNode().hasProperty("name")) {

      val label = each.endNode().getProperty("name").asInstanceOf[String]

      if (each.length() > stack.length - 1) {
        stack.push(toNode(label))
      } else if (each.length() < stack.length - 1) {
        while (each.length() < stack.length) {
          stack.pop()
        }

        stack.top.children += toNode(label)
      } else {
        stack.top.children += toNode(label)
      }
    }

    stack.apply(0)
  }

  private def toNode(nodeName: String) = {
    TreeNode(nodeName.replaceFirst("http://", "").replaceFirst("https://", ""))
  }

}

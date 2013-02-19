package cz.pechdavid.mycelium.core.operator

import cz.pechdavid.mycelium.core.module.ModuleSpec
import org.jgrapht.experimental.dag.DirectedAcyclicGraph
import org.jgrapht.graph.{DefaultEdge, SimpleDirectedGraph}
import org.jgrapht.alg.CycleDetector
import org.jgrapht.traverse.{DepthFirstIterator, TopologicalOrderIterator}
import scala.collection.JavaConversions._

/**
 * Created: 2/15/13 5:58 PM
 */
class DependencyLinearizer(availableModules: Set[ModuleSpec]) {


  val graph = prepareGraph(availableModules)

  val cycleDetector = new CycleDetector(graph)
  require(!cycleDetector.detectCycles())


  private def prepareGraph(mods: Set[ModuleSpec]) = {
    val graph = new SimpleDirectedGraph[String, DefaultEdge](classOf[DefaultEdge])

    mods.foreach {
      each =>
        if (!graph.containsVertex(each.name)) {
          graph.addVertex(each.name)
        }

        for (
          req <- each.requirements
          if (!graph.containsEdge(req, each.name))
        ) {
          if (!graph.containsVertex(req)) {
            graph.addVertex(req)
          }

          graph.addEdge(each.name, req)
        }
    }

    graph
  }

  def calculate(running: Set[String], desired: List[String]): List[String] = {
    val requiredDeps = desired.map{
      vertex =>
        new DepthFirstIterator(graph, vertex).toList
    }.flatten.distinct

    requiredDeps.filter {
      !running.contains(_)
    }.reverse
  }
}























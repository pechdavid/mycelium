/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.node.SystemNode
import akka.actor.Props
import cz.pechdavid.mycelium.core.module.ModuleSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:10 PM
 */
@RunWith(classOf[JUnitRunner])
class NodeCommunication extends FlatSpec with ShouldMatchers {

  it should "Flexible keep the run list up" in {
    val allMods = Map("A" -> ((_: ModuleSpec) => Props[EmptyActor]), "B" -> ((_: ModuleSpec) => Props[EmptyActor]),
      "C" -> ((_: ModuleSpec) => Props[EmptyActor]))
    val nodeA = new SystemNode(allMods)
    val nodeB = new SystemNode(allMods)
    val nodeC = new SystemNode(allMods)

    val spec = Set(ModuleSpec("A", Set.empty), ModuleSpec("B", Set.empty), ModuleSpec("C", Set.empty))

    nodeA.boot(spec, List("A", "B", "C"))
    nodeB.boot(spec, List("A", "B", "C"))
    nodeC.boot(spec, List("A", "B", "C"))

    Thread.sleep(5000)

    nodeA.container.globalNodes.size should be(3)
    nodeA.container.globalRunning should be(Set("A", "B", "C"))
    nodeB.container.globalNodes.size should be(3)
    nodeB.container.globalRunning should be(Set("A", "B", "C"))
    nodeC.container.globalNodes.size should be(3)
    nodeC.container.globalRunning should be(Set("A", "B", "C"))
    Set(nodeA, nodeB, nodeC).map {
      _.container.localRunning.size
    }.sum should be(3)

    nodeA.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(2)
    nodeB.container.globalRunning should be(Set("A", "B", "C"))
    nodeC.container.globalNodes.size should be(2)
    nodeC.container.globalRunning should be(Set("A", "B", "C"))
    Set(nodeB, nodeC).map {
      _.container.localRunning.size
    }.sum should be(3)

    nodeC.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(1)
    nodeB.container.globalRunning should be(Set("A", "B", "C"))
    Set(nodeB).map {
      _.container.localRunning.size
    }.sum should be(3)

    nodeB.shutdown()
  }

  it should "Flexible keep the run list up only" in {
    val allMods = Map("A" -> ((_: ModuleSpec) => Props[EmptyActor]), "B" -> ((_: ModuleSpec) => Props[EmptyActor]),
      "C" -> ((_: ModuleSpec) => Props[EmptyActor]))
    val nodeA = new SystemNode(allMods)
    val nodeB = new SystemNode(allMods)
    val nodeC = new SystemNode(allMods)

    val spec = Set(ModuleSpec("A", Set.empty), ModuleSpec("B", Set.empty), ModuleSpec("C", Set.empty))

    nodeA.boot(spec, List("A"))
    nodeB.boot(spec, List("B"))
    nodeC.boot(spec, List("C"))

    Thread.sleep(5000)

    nodeA.container.globalNodes.size should be(3)
    nodeA.container.globalRunning should be(Set("A", "B", "C"))
    nodeB.container.globalNodes.size should be(3)
    nodeB.container.globalRunning should be(Set("A", "B", "C"))
    nodeC.container.globalNodes.size should be(3)
    nodeC.container.globalRunning should be(Set("A", "B", "C"))
    Set(nodeA, nodeB, nodeC).map {
      _.container.localRunning.size
    } should be(Set(1, 1, 1))

    nodeA.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(2)
    nodeB.container.globalRunning should be(Set("B", "C"))
    nodeC.container.globalNodes.size should be(2)
    nodeC.container.globalRunning should be(Set("B", "C"))
    Set(nodeB, nodeC).map {
      _.container.localRunning.size
    } should be(Set(1, 1))

    nodeC.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(1)
    nodeB.container.globalRunning should be(Set("B"))
    Set(nodeB).map {
      _.container.localRunning.size
    } should be(Set(1))

    nodeB.shutdown()
  }

  it should "Dynamically start up dependencies" in {
    val allMods = Map("A" -> ((_: ModuleSpec) => Props[EmptyActor]), "B" -> ((_: ModuleSpec) => Props[EmptyActor]))
    val nodeA = new SystemNode(allMods)
    val nodeB = new SystemNode(allMods)
    val nodeC = new SystemNode(allMods)

    val spec = Set(ModuleSpec("A", Set.empty), ModuleSpec("B", Set("A")))

    nodeA.boot(spec, List.empty)
    nodeB.boot(spec, List.empty)
    nodeC.boot(spec, List("B"))

    Thread.sleep(5000)

    nodeA.container.globalNodes.size should be(3)
    nodeA.container.globalRunning should be(Set("A", "B"))
    nodeB.container.globalNodes.size should be(3)
    nodeB.container.globalRunning should be(Set("A", "B"))
    nodeC.container.globalNodes.size should be(3)
    nodeC.container.globalRunning should be(Set("A", "B"))
    Set(nodeA, nodeB, nodeC).map {
      _.container.localRunning.size
    }.sum should be(2)

    nodeA.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(2)
    nodeB.container.globalRunning should be(Set("A", "B"))
    nodeC.container.globalNodes.size should be(2)
    nodeC.container.globalRunning should be(Set("A", "B"))
    Set(nodeB, nodeC).map {
      _.container.localRunning.size
    }.sum should be(2)

    nodeC.shutdown()

    Thread.sleep(5000)

    nodeB.container.globalNodes.size should be(0)
    nodeB.container.globalRunning should be(Set.empty)
    Set(nodeB).map {
      _.container.localRunning.size
    } should be(Set.empty)

    nodeB.shutdown()
  }
}

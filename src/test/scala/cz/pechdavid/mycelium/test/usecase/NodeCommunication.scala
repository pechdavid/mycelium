package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.node.SystemNode
import akka.actor.Props
import cz.pechdavid.mycelium.core.module.ModuleProps
import cz.pechdavid.mycelium.core.module.ModuleSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:10 PM
 */
@RunWith(classOf[JUnitRunner])
class NodeCommunication extends FlatSpec with ShouldMatchers {

  it should "Simulate node interactions" in {
    // FIXME: HERE
  }

  it should "Start multiple workers when other fails" in {
    // FIXME: HERE
  }

  it should "Be variable about node run list" in {
    // FIXME: start with lower ping timeout
    val nodeA = new SystemNode
    val nodeB = new SystemNode
    val nodeC = new SystemNode

    val allMods = Map("A" -> Props[EmptyActor], "B" -> Props[EmptyActor], "C" -> Props[EmptyActor])

    val spec = Set(ModuleSpec("A", Set.empty), ModuleSpec("B", Set.empty), ModuleSpec("C", Set.empty))

    nodeA.registerProps(allMods)
    nodeB.registerProps(allMods)
    nodeC.registerProps(allMods)

    nodeA.boot(spec, List(ModuleProps("A", Map.empty)))
    nodeB.boot(spec, List(ModuleProps("B", Map.empty)))
    nodeC.boot(spec, List(ModuleProps("C", Map.empty)))

    Thread.sleep(100)

    nodeA.globalNodes.size should be(3)
    nodeA.globalRunning should be(Set("A", "B", "C"))
    nodeB.globalNodes.size should be(3)
    nodeB.globalRunning should be(Set("A", "B", "C"))
    nodeC.globalNodes.size should be(3)
    nodeC.globalRunning should be(Set("A", "B", "C"))

    nodeA.shutdown()

    Thread.sleep(100)

    nodeB.globalNodes.size should be(2)
    nodeB.globalRunning should be(Set("A", "B", "C"))
    nodeC.globalNodes.size should be(2)
    nodeC.globalRunning should be(Set("A", "B", "C"))

    nodeC.shutdown()

    Thread.sleep(100)

    nodeB.globalNodes.size should be(1)
    nodeB.globalRunning should be(Set("A", "B", "C"))

    nodeB.shutdown()
  }
}

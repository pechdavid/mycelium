package cz.pechdavid.mycelium.test.usecase

import cz.pechdavid.mycelium.core.node.SystemNode
import java.util.concurrent.LinkedBlockingDeque
import akka.testkit.TestActor
import cz.pechdavid.mycelium.core.module._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.core.module.ModuleProps
import cz.pechdavid.mycelium.core.module.DependencyNotOnline
import akka.actor.{Props, Actor}

/**
 * Created: 2/15/13 6:10 PM
 */
class NodeCrash extends FlatSpec with ShouldMatchers {

  it should "Return error message after timeout and recover" in {
    // FIXME: start with lower ping timeout
    val nodeA = new SystemNode
    val nodeB = new SystemNode
    val queue = new LinkedBlockingDeque[TestActor.Message]()

    nodeB.registerProps(Map("B" -> Props[EmptyActor]))
    nodeB.boot(Set(ModuleSpec("B", Set.empty)), List(ModuleProps("B", Map.empty)))
    nodeA.registerProps(Map("A" -> TestActor.props(queue)))
    nodeA.boot(Set(ModuleSpec("A", Set("B"))), List(ModuleProps("A", Map.empty)))

    Thread.sleep(100)

    queue.size() should be(2)
    queue.removeLast().msg should be(PostInitialize)
    queue.removeLast().msg should be(StartModule)

    nodeB.shutdown()

    Thread.sleep(100)

    queue.size() should be(1)
    queue.removeLast().msg should be(DependencyNotOnline("B"))

    val nodeC = new SystemNode
    nodeC.registerProps(Map("B" -> Props[EmptyActor]))
    nodeC.boot(Set(ModuleSpec("B", Set.empty)), List(ModuleProps("B", Map.empty)))

    Thread.sleep(100)

    queue.size() should be(1)
    queue.removeLast().msg should be(RecoverModule)

    nodeA.shutdown()
    nodeC.shutdown()
  }

}

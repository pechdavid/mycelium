package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.{TestActor, TestKit, TestProbe}
import cz.pechdavid.mycelium.core.node.SystemNode
import akka.actor.{ActorSystem, Props}
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.mycelium.core.module._
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.core.module.ModuleProps
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:50 PM
 */
@RunWith(classOf[JUnitRunner])
class ModuleLifecycle extends FlatSpec with ShouldMatchers {

  it should "Demonstrate lifecycle" in {

    val queue = new LinkedBlockingDeque[TestActor.Message]()

    val node = new SystemNode(Map("A" -> ((_) => TestActor.props(queue))))
    node.boot(Set(ModuleSpec("A", Set.empty)), List(ModuleProps("A", None)))

    Thread.sleep(500)

    queue.size() should be(2)
    queue.removeFirst().msg should be(PostInitialize)
    queue.removeFirst().msg should be(StartModule)

    node.shutdown()

    queue.size() should be(2)
    queue.removeFirst().msg should be(StopModule)
    queue.removeFirst().msg should be(PostStop)
  }

  it should "Return error message after timeout" in {
    // FIXME: start with lower timeout
    val queue = new LinkedBlockingDeque[TestActor.Message]()

    val node = new SystemNode(Map("A" -> ((_) => TestActor.props(queue))))
    // missing dep!
    node.boot(Set(ModuleSpec("A", Set("B"))), List(ModuleProps("A", None)))

    Thread.sleep(100)

    queue.size() should be(2)
    queue.removeFirst().msg should be(PostInitialize)
    queue.removeFirst().msg should be(DependencyNotOnline("B"))

    node.shutdown()
  }

}

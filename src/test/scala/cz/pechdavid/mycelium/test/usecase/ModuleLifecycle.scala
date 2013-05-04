package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.testkit.TestActor
import cz.pechdavid.mycelium.core.node.SystemNode
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.mycelium.core.module._
import cz.pechdavid.mycelium.core.module.ModuleSpec
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
    node.boot(Set(ModuleSpec("A", Set.empty)), List("A"))

    Thread.sleep(500)

    queue.size() should be(2)
    queue.removeFirst().msg should be(PostInitialize)
    queue.removeFirst().msg should be(StartModule)

    node.shutdown()

    Thread.sleep(2000)

    queue.size() should be(2)
    queue.removeFirst().msg should be(StopModule)
    queue.removeFirst().msg should be(PostStop)
  }

  it should "Return error message after timeout" in {
    val queue = new LinkedBlockingDeque[TestActor.Message]()

    val node = new SystemNode(Map("A" -> ((_) => TestActor.props(queue))))
    // missing dep!
    node.boot(Set(ModuleSpec("A", Set("B"))), List("A"))

    Thread.sleep(4000)

    queue.size() should be(3)
    queue.removeFirst().msg should be(PostInitialize)
    queue.removeFirst().msg should be(StartModule)
    queue.removeFirst().msg should be(DependencyNotOnline("B"))

    node.shutdown()
  }

}

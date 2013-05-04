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
import cz.pechdavid.mycelium.core.module._
import akka.testkit.TestActor
import java.util.concurrent.LinkedBlockingDeque
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.core.module.PostInitialize

/**
 * Created: 2/15/13 6:10 PM
 */
@RunWith(classOf[JUnitRunner])
class SeamlessCommunication extends FlatSpec with ShouldMatchers {

  class SendToB extends ProducerModule("A") {
    def handle = {
      case StartModule =>
        Thread.sleep(500)

        moduleRef("B") ! TstMessage("hello")
    }
  }

  it should "Work on the same node" in {
    val queue = new LinkedBlockingDeque[TestActor.Message]()

    val system = new SystemNode(Map("A" -> ((_) => Props(new SendToB)),
      "B" -> ((_) => Props(new ConsumingTstModule("B", queue)))))
    system.boot(Set(ModuleSpec("A", Set("B")),
      ModuleSpec("B", Set.empty)), List("B", "A"))

    Thread.sleep(1000)

    sleepAndCheckQueue(queue)

    system.shutdown()
  }

  it should "Seamlessly send messages" in {

    val queue = new LinkedBlockingDeque[TestActor.Message]()
    val systemB = new SystemNode(Map("B" -> ((_) => Props(new ConsumingTstModule("B", queue)))))

    systemB.boot(Set(ModuleSpec("B", Set.empty)), List("B"))

    val systemA = new SystemNode(Map("A" -> ((_) => Props(new SendToB))))
    systemA.boot(Set(ModuleSpec("A", Set("B"))), List("A"))

    sleepAndCheckQueue(queue)

    systemA.shutdown()
    systemB.shutdown()
  }


  def sleepAndCheckQueue(queue: LinkedBlockingDeque[TestActor.Message]) {
    Thread.sleep(2000)

    queue.size() should be(3)
    queue.removeFirst().msg should be(PostInitialize)
    queue.removeFirst().msg should be(StartModule)
    queue.removeFirst().msg should be(TstMessage("hello"))
  }

  it should "Seamlessly send messages in other order of registration" in {

    val queue = new LinkedBlockingDeque[TestActor.Message]()

    // opposite order!
    val systemA = new SystemNode(Map("A" -> ((_) => Props(new SendToB))))
    systemA.boot(Set(ModuleSpec("A", Set("B"))), List("A"))

    val systemB = new SystemNode(Map("B" -> ((_) => Props(new ConsumingTstModule("B", queue)))))
    systemB.boot(Set(ModuleSpec("B", Set.empty)), List("B"))

    sleepAndCheckQueue(queue)

    systemA.shutdown()
    systemB.shutdown()


  }
}

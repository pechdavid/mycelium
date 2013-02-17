package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.node.SystemNode
import akka.actor.{Props, Actor}
import cz.pechdavid.mycelium.core.module.{PostInitialize, ModuleProps, ModuleSpec, StartModule}
import akka.testkit.TestActor
import java.util.concurrent.LinkedBlockingDeque
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.specs2.internal.scalaz.Digit._0

/**
 * Created: 2/15/13 6:10 PM
 */
@RunWith(classOf[JUnitRunner])
class SeamlessCommunication extends FlatSpec with ShouldMatchers {

  case object TstMessage

  class SendToB extends Actor {
    def receive = {
      case StartModule =>
        context.actorFor("B") ! TstMessage
    }
  }

  it should "Work on the same node" in {
    val system = new SystemNode

    val queue = new LinkedBlockingDeque[TestActor.Message]()

    system.registerProps(Map("A" -> ((_) => Props[SendToB]),
      "B" -> ((_) => TestActor.props(queue))))
    system.boot(Set(ModuleSpec("A", Set("B")),
      ModuleSpec("B", Set.empty)), List(ModuleProps("A", None)))

    sleepAndCheckQueue(queue)

    system.shutdown()
  }

  it should "Seamlessly send messages" in {

    val systemA = new SystemNode
    val systemB = new SystemNode

    val queue = new LinkedBlockingDeque[TestActor.Message]()
    systemB.registerProps(Map("B" -> ((_) => TestActor.props(queue))))

    systemB.boot(Set(ModuleSpec("B", Set.empty)), List(ModuleProps("B", None)))

    systemA.registerProps(Map("A" -> ((_) => Props[SendToB])))
    systemA.boot(Set(ModuleSpec("A", Set("B"))), List(ModuleProps("A", None)))

    sleepAndCheckQueue(queue)

    systemA.shutdown()
    systemB.shutdown()
  }


  def sleepAndCheckQueue(queue: LinkedBlockingDeque[TestActor.Message]) {
    Thread.sleep(100)

    queue.size() should be(3)
    queue.removeLast().msg should be(PostInitialize)
    queue.removeLast().msg should be(StartModule)
    queue.removeLast().msg should be(TstMessage)
  }

  it should "Seamlessly send messages in other order of registration" in {
    val systemA = new SystemNode
    val systemB = new SystemNode

    val queue = new LinkedBlockingDeque[TestActor.Message]()

    // opposite order!
    systemA.registerProps(Map("A" -> ((_) => Props[SendToB])))
    systemA.boot(Set(ModuleSpec("A", Set("B"))), List(ModuleProps("A", None)))

    systemB.registerProps(Map("B" -> ((_) => TestActor.props(queue))))
    systemB.boot(Set(ModuleSpec("B", Set.empty)), List(ModuleProps("B", None)))

    sleepAndCheckQueue(queue)

    systemA.shutdown()
    systemB.shutdown()


  }
}

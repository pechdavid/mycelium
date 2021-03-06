/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.usecase

import cz.pechdavid.mycelium.core.node.SystemNode
import java.util.concurrent.{BlockingDeque, LinkedBlockingDeque}
import akka.testkit.TestActor
import cz.pechdavid.mycelium.core.module.ModuleSpec
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Props, Actor}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:14 PM
 */
@RunWith(classOf[JUnitRunner])
class ExceptionSurvival extends FlatSpec with ShouldMatchers {

  case object ThrowExceptionCausingRestart

  case object CorrectMessage

  class TrapActor(queue: BlockingDeque[TestActor.Message]) extends Actor {
    def receive = {
      case ThrowExceptionCausingRestart =>
        throw new RuntimeException("ERR")

      case other: AnyRef =>
        queue.add(TestActor.RealMessage(other, sender))
    }
  }

  it should "Survive exception" in {

    val queue = new LinkedBlockingDeque[TestActor.Message]()

    val system = new SystemNode(Map("A" -> ((_) => Props(new TrapActor(queue)))))
    system.boot(Set(ModuleSpec("A", Set.empty)), List("A"))

    Thread.sleep(100)

    queue.clear()

    val ref = system.moduleRef("A")

    ref ! CorrectMessage

    Thread.sleep(5000)

    queue.removeLast().msg should be(CorrectMessage)

    ref ! ThrowExceptionCausingRestart

    ref ! CorrectMessage

    Thread.sleep(5000)

    queue.removeLast().msg should be(CorrectMessage)
    queue.size() should be(0)

    system.shutdown()
  }

}

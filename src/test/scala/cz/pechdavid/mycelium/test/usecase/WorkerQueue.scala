package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import cz.pechdavid.mycelium.core.node.SystemNode
import java.util.concurrent.LinkedBlockingDeque
import akka.testkit.TestActor
import akka.actor.Props
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.mycelium.core.messaging.RoundRobinQueue
import scala.collection.JavaConversions._

/**
 * Created: 2/15/13 9:37 PM
 */
@RunWith(classOf[JUnitRunner])
class WorkerQueue extends FlatSpec with ShouldMatchers {

  it should "Register separate node and receive messages over it" in {
    val queue1 = new LinkedBlockingDeque[TestActor.Message]()
    val queue2 = new LinkedBlockingDeque[TestActor.Message]()

    val system = new SystemNode(Map(
      "A1" -> ((_) => Props(new ConsumingTstModule("A1", queue1))),
      "A2" -> ((_) => Props(new ConsumingTstModule("A2", queue2))),
      "B" -> ((_) => Props(new RoundRobinQueue("B", List("A1", "A2"))))))

    system.boot(Set(ModuleSpec("A1", Set.empty), ModuleSpec("A2", Set.empty),
      ModuleSpec("B", Set("A1", "A2"))),
      List("A1", "A2", "B"))

    Thread.sleep(1000)

    queue1.clear()
    queue2.clear()

    system.moduleRef("B") ! TstMessage("1")
    system.moduleRef("B") ! TstMessage("2")
    system.moduleRef("B") ! TstMessage("3")
    system.moduleRef("B") ! TstMessage("4")
    system.moduleRef("B") ! TstMessage("5")

    Thread.sleep(500)

    queue1.map {
      _.msg
    }.toList should be(List(TstMessage("1"), TstMessage("3"), TstMessage("5")))

    queue2.map {
      _.msg
    }.toList should be(List(TstMessage("2"), TstMessage("4")))

    system.shutdown()
  }

}

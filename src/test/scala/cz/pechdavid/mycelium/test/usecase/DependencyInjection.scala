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
import cz.pechdavid.mycelium.core.module.ModuleSpec
import akka.actor.{Actor, Props}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:10 PM
 */
@RunWith(classOf[JUnitRunner])
class DependencyInjection extends FlatSpec with ShouldMatchers {

  class TstA extends Actor {
    def receive = {
      case _: AnyRef =>
      // pass
    }
  }

  class TstB extends Actor {
    def receive = {
      case _: AnyRef =>
      // pass
    }
  }

  it should "Boot env" in {
    val node = new SystemNode(Map("A" -> ((_) => Props(new TstA)),
      "B" -> ((_) => Props(new TstB))))

    node.boot(Set(
      ModuleSpec("A", Set.empty),
      ModuleSpec("B", Set("A"))), List("B"))

    Thread.sleep(2000)

    node.container.globalRunning should be(Set("A", "B"))
  }
}

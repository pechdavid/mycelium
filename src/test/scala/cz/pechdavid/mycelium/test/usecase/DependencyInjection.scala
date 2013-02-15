package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec}
import akka.actor.{Actor, Props}

/**
 * Created: 2/15/13 6:10 PM
 */
class DependencyInjection extends FlatSpec with ShouldMatchers {

  class TstA extends  Actor {
    def receive = ???
  }

  class TstB extends  Actor {
    def receive = ???
  }

  it should "Boot env" in {
    val node = new SystemNode(1)

    node.registerProps(Map("A" -> Props[TstA],
      "B" -> Props[TstB]))

    node.boot(Set(
      ModuleSpec("A", Set.empty),
      ModuleSpec("B", Set("A"))), List(ModuleProps("B", Map.empty)))


    node.running should be(Set("A", "B"))
  }
}

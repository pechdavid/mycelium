package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Props, Actor}
import collection.mutable
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec, StartModule}
import cz.pechdavid.mycelium.core.command.{RegisterCommandHandler, CommandBus}
import cz.pechdavid.mycelium.core.event.{RegisterEventHandler, EventBus}

/**
 * Created: 2/15/13 6:11 PM
 */
class CommandPipeline extends FlatSpec with ShouldMatchers {

  case class MyCommand(name: String)
  case class MyCommandCalled(name: String)
  case class Store(name: String)

  class MyCommandHandler extends Actor {
    def receive = {
      case StartModule =>
        context.actorFor("commandBus") ! RegisterCommandHandler
      case MyCommand(name) =>
        context.actorFor("eventBus") ! MyCommandCalled(name)
    }
  }

  class MyCommandCalledHandler extends Actor {
    def receive = {
      case StartModule =>
        context.actorFor("eventBus") ! RegisterEventHandler
      case MyCommandCalled(name) =>
        context.actorFor("inMemoryProjection") ! Store(name)
    }
  }

  class InMemoryProjection(lst: mutable.MutableList[String]) extends Actor {
    def receive = {
      case Store(n) =>
        lst += n
    }
  }

  it should "Fall through the pipeline" in {
    val lst = mutable.MutableList.empty[String]

    val system = new SystemNode
    system.registerProps(Map("eventBus" -> Props[EventBus],
      "commandBus" -> Props[CommandBus],
      "myCommandHandler" -> Props[MyCommandHandler],
      "myCommandCalledHandler" -> Props[MyCommandCalledHandler],
      "inMemoryProjection" -> Props(new InMemoryProjection(lst))))

    system.boot(
      Set(
        ModuleSpec("eventBus", Set.empty),
        ModuleSpec("commandBus", Set("eventBus")),
        ModuleSpec("myCommandHandler", Set("commandBus", "eventBus")),
        ModuleSpec("myCommandCalledHandler", Set("eventBus", "inMemoryProjection")),
        ModuleSpec("inMemoryProjection", Set.empty)
      ),
      List(
        ModuleProps("inMemoryProjection", Map.empty),
        ModuleProps("myCommandHandler", Map.empty),
        ModuleProps("myCommandCalledHandler", Map.empty)
      )
    )

    system.system.actorFor("commandBus") ! MyCommand("Hello")

    Thread.sleep(100)

    lst.headOption should be(Some("Hello"))

    system.shutdown()
  }



}

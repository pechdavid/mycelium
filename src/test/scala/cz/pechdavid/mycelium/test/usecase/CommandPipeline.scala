package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Props, Actor}
import collection.mutable
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec, StartModule}
import cz.pechdavid.mycelium.core.command.{CommandHandler, RegisterCommandHandler, CommandBus}
import cz.pechdavid.mycelium.core.event.{EventHandler, RegisterEventHandler, EventBus}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:11 PM
 */
@RunWith(classOf[JUnitRunner])
class CommandPipeline extends FlatSpec with ShouldMatchers {

  case class MyCommand(name: String)
  case class MyCommandCalled(name: String)
  case class Store(name: String)

  class MyCommandHandler extends CommandHandler {
    def handle = {
      case MyCommand(name) =>
        List(MyCommandCalled(name))
    }
  }

  class MyCommandCalledHandler extends EventHandler {
    def handle = {
      case MyCommandCalled(name) =>
        notifyProjections(Set("inMemoryProjection"), Store(name))
    }
  }

  class MyApp extends Actor {
    def receive = {
      case StartModule =>
        context.actorFor("commandBus") ! RegisterCommandHandler(new MyCommandHandler)
        context.actorFor("eventBus") ! RegisterEventHandler(new MyCommandCalledHandler)
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
    system.registerProps(Map("eventBus" -> ((_) => Props[EventBus]),
      "commandBus" -> ((_) => Props[CommandBus]),
      "myApp" -> ((_) => Props[MyApp]),
      "inMemoryProjection" -> ((_) => Props(new InMemoryProjection(lst)))))

    system.boot(
      Set(
        ModuleSpec("eventBus", Set.empty),
        ModuleSpec("commandBus", Set("eventBus")),
        ModuleSpec("myApp", Set("commandBus", "eventBus", "inMemoryProjection")),
        ModuleSpec("inMemoryProjection", Set.empty)
      ),
      List(
        ModuleProps("inMemoryProjection", None),
        ModuleProps("myApp", None)
      )
    )

    system.system.actorFor("commandBus") ! MyCommand("Hello")

    Thread.sleep(100)

    lst.headOption should be(Some("Hello"))

    system.shutdown()
  }
}

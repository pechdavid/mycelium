/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import akka.actor.{Props, Actor}
import collection.mutable
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.{ModuleRef, ModuleSpec, StartModule}
import cz.pechdavid.mycelium.core.command.{CommandHandler, RegisterCommandHandler, CommandBus}
import cz.pechdavid.mycelium.core.event.{Event, EventHandler, RegisterEventHandler, EventBus}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import cz.pechdavid.mycelium.core.projection.NotifyProjections

/**
 * Created: 2/15/13 6:11 PM
 */
@RunWith(classOf[JUnitRunner])
class CommandPipeline extends FlatSpec with ShouldMatchers {

  case class MyCommand(name: String)

  case class MyCommandCalled(name: String) extends Event

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
        List(NotifyProjections(Set("inMemoryProjection"), Store(name)))
    }
  }

  class MyApp extends ModuleRef {
    def receive = {
      case StartModule =>
        moduleRef("commandBus") ! RegisterCommandHandler(new MyCommandHandler)
        moduleRef("eventBus") ! RegisterEventHandler(new MyCommandCalledHandler)
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

    val system = new SystemNode(Map("eventBus" -> ((_) => Props[EventBus]),
      "commandBus" -> ((_) => Props[CommandBus]),
      "myApp" -> ((_) => Props(new MyApp)),
      "inMemoryProjection" -> ((_) => Props(new InMemoryProjection(lst)))))

    system.boot(
      Set(
        ModuleSpec("eventBus", Set.empty),
        ModuleSpec("commandBus", Set("eventBus")),
        ModuleSpec("myApp", Set("commandBus", "eventBus", "inMemoryProjection")),
        ModuleSpec("inMemoryProjection", Set.empty)
      ),
      List(
        "inMemoryProjection", "myApp"
      )
    )

    Thread.sleep(1000)

    system.moduleRef("commandBus") ! MyCommand("Hello")

    Thread.sleep(1000)

    lst.headOption should be(Some("Hello"))

    system.shutdown()
  }
}

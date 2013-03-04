package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.command.{RegisterCommandHandler, CommandBus, CommandHandler}
import cz.pechdavid.mycelium.core.event.{RegisterEventHandler, EventBus, EventHandler}
import akka.actor.Props
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/22/13 6:05 PM
 */
class WebWeaver(launchPatterns: Map[String, (ModuleSpec) => Props] = Map.empty, modules: List[ModuleSpec] = Nil,
                cmds: List[CommandHandler] = List.empty, evs: List[EventHandler] = List.empty) {

  val node = new SystemNode(
    Map(
      "commandBus" -> ((_: ModuleSpec) => Props[CommandBus]),
      "eventBus" -> ((_: ModuleSpec) => Props[EventBus])
    ) ++ launchPatterns
  )

  node.boot(Set(ModuleSpec("queue", Set("commandBus", "eventBus")),
    ModuleSpec("commandBus", Set("eventBus")), ModuleSpec("eventBus")) ++ modules,
    List("commandBus") ++ (modules.map(_.name))
  )

  Thread.sleep(200)

  for (handler <- cmds) {
    node.moduleRef("commandBus") ! RegisterCommandHandler(handler)
  }

  for (handler <- evs) {
    node.moduleRef("eventBus") ! RegisterEventHandler(handler)
  }
}

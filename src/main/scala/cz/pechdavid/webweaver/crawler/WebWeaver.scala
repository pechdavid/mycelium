package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.command.{RegisterCommandHandler, CommandBus, CommandHandler}
import cz.pechdavid.mycelium.core.event.{RegisterEventHandler, EventBus, EventHandler}
import akka.actor.Props
import cz.pechdavid.mycelium.core.node.SystemNode
import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec}

/**
 * Created: 2/22/13 6:05 PM
 */
class WebWeaver(queue: Option[Props], cmds: List[CommandHandler], evs: List[EventHandler]) {

  val node = new SystemNode(
    Map(
      "commandBus" -> ((_: ModuleProps) => Props[CommandBus]),
      "eventBus" -> ((_: ModuleProps) => Props[EventBus])
    ) ++ (
      if (queue.isDefined) {
        Map("queue" -> ((_: ModuleProps) => queue.get))
      } else {
        Map()
      }
    )
  )

  node.boot(Set(ModuleSpec("queue", Set("commandBus", "eventBus")),
    ModuleSpec("commandBus", Set("eventBus"))),
    List(ModuleProps("commandBus")) ++ (if (queue.isDefined)  List(ModuleProps("queue")) else List.empty)
  )

  Thread.sleep(200)

  for (handler <- cmds) {
    node.moduleRef("commandBus") ! RegisterCommandHandler(handler)
  }

  for (handler <- evs) {
    node.moduleRef("eventBus") ! RegisterEventHandler(handler)
  }
}

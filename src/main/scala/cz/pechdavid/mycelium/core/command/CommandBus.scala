package cz.pechdavid.mycelium.core.command

import akka.actor.Actor
import cz.pechdavid.mycelium.core.module.ModuleRef
import cz.pechdavid.mycelium.core.event.EventPack

/**
 * Created: 2/15/13 11:59 PM
 */
class CommandBus extends Actor with ModuleRef {

  var handlers = Set.empty[CommandHandler]


  def receive = {
    case RegisterCommandHandler(handler) =>
      handlers += handler

    case cmd: AnyRef =>
      val eventBus = moduleRef("eventBus")

      handlers.filter {
        handler =>
          handler.handle.isDefinedAt(cmd)
      }.map {
        handler =>
          handler.handle(cmd)
      }.foreach {
        lst =>
          if (!lst.isEmpty) {
            eventBus ! EventPack(lst)
          }
      }
  }
}

package cz.pechdavid.mycelium.core.command

import akka.actor.Actor
import cz.pechdavid.mycelium.core.module.ModuleRef
import cz.pechdavid.mycelium.core.event.EventPack
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/15/13 11:59 PM
 */
class CommandBus extends Actor with ModuleRef with SLF4JLogging {

  var handlers = Set.empty[CommandHandler]


  def receive = {
    case RegisterCommandHandler(handler) =>
      handlers += handler

    case cmd: AnyRef =>
      val eventBus = moduleRef("eventBus")

      log.info("Command: " + cmd.toString)

      handlers.filter {
        handler =>
          handler.handle.isDefinedAt(cmd)
      }.map {
        handler =>
          try {
            handler.handle(cmd)
          } catch {
            case ex: Exception =>
              List.empty
          }
      }.foreach {
        lst =>
          if (!lst.isEmpty) {
            eventBus ! EventPack(lst)
          }
      }
  }
}

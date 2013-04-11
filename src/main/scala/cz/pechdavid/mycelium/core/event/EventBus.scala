package cz.pechdavid.mycelium.core.event

import akka.actor.Actor
import cz.pechdavid.mycelium.core.module.ModuleRef

/**
 * Created: 2/15/13 11:59 PM
 */
class EventBus extends Actor with ModuleRef {

  var handlers = Set.empty[EventHandler]

  def receive = {
    case RegisterEventHandler(handler) =>
      handlers += handler

    case EventPack(events) =>
      for (ev <- events) {
        handlers.filter {
          _.handle.isDefinedAt(ev)
        }.map {
          op =>
            try {
              op.handle(ev)
            } catch {
              case ex: Exception =>
                Nil
            }
        }.foreach {
          for (notifyEvent <- _;
               mod <- notifyEvent.projections;
               ref = moduleRef(mod)) {
            ref ! notifyEvent.msg
          }
        }
      }
  }
}

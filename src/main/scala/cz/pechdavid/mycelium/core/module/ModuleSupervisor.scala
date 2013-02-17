package cz.pechdavid.mycelium.core.module

import akka.actor.{ActorRef, Actor}

/**
 * Created: 2/17/13 6:34 PM
 */
class ModuleSupervisor extends Actor {

  // FIXME: supervisor strategy

  def receive = {
    case StartNewModule(name, props) =>
      context.actorOf(props, name)

    case Forward(name, msg) =>
      context.child(name) match {
        case Some(ar: ActorRef) =>
          ar ! msg
      }
  }
}

package cz.pechdavid.mycelium.test.usecase

import akka.actor.Actor

/**
 * Created: 2/15/13 11:44 PM
 */
class EmptyActor extends Actor {
  def receive = {
    case x: AnyRef =>
    // pass
  }
}



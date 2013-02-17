package cz.pechdavid.mycelium.core.module

import akka.actor.{Actor, ActorRef}

/**
 * Created: 2/17/13 8:57 PM
 */
trait ModuleRef extends Actor {

  def moduleRef(name: String): ActorRef = {
    context.actorFor("/user/supervisor/" + name)
  }

}

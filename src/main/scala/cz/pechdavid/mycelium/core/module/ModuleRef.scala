package cz.pechdavid.mycelium.core.module

import akka.actor.{Actor, ActorRef}

/**
 * Created: 2/17/13 8:57 PM
 */
object ModuleRef {
  val ModulePathPrefix = "/user/supervisor/"
}

trait ModuleRef extends Actor {

  def moduleRef(name: String): ActorRef = {
    context.actorFor(ModuleRef.ModulePathPrefix + name)
  }

}

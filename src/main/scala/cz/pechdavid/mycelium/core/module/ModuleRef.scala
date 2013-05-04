/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.module

import akka.actor.{Actor, ActorRef}

/**
 * Module reference interface
 *
 * Created: 2/17/13 8:57 PM
 */
object ModuleRef {
  val ModulePathPrefix = "/user/supervisor/"
}

/**
 * Module reference implementation
 */
trait ModuleRef extends Actor {

  def moduleRef(name: String): ActorRef = {
    context.actorFor(ModuleRef.ModulePathPrefix + name)
  }

}

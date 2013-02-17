package cz.pechdavid.mycelium.core.messaging

import akka.actor.Actor

/**
 * Created: 2/17/13 8:27 PM
 */
trait ProducerProxy extends Actor {

  def producerRef = context.actorFor("/user/producer")

}

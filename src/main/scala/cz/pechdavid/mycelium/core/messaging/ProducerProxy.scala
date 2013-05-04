/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.messaging

import akka.actor.Actor

/**
 * Actor with link to the producer
 *
 * Created: 2/17/13 8:27 PM
 */
trait ProducerProxy extends Actor {

  def producerRef = context.actorFor("/user/producer")

}

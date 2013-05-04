/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.messaging.ProducerProxy
import akka.amqp.{Message, PublishToExchange}

/**
 * Forwarding module used when implementation is running on a different node.
 *
 * Created: 2/17/13 8:31 PM
 */
class ForwardModule(name: String) extends ProducerProxy {
  def receive = {
    case _: ControlMessage =>
      // pass

    case other: AnyRef =>
      producerRef ! PublishToExchange(Message(other, name), "worker")
  }
}

package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.messaging.ProducerProxy
import akka.amqp.{Message, PublishToExchange}

/**
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

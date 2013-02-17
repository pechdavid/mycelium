package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.messaging.ProducerProxy
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/17/13 9:19 PM
 */
abstract class ProducerModule(name: String) extends ProducerProxy with ModuleRef with SLF4JLogging {

  def handle: PartialFunction[AnyRef, Unit]

  def receive = {
    case other: AnyRef =>
      if (handle.isDefinedAt(other)) {
        handle(other)
      } else {
        log.debug("Message not handled: " + other.toString)
      }
  }
}

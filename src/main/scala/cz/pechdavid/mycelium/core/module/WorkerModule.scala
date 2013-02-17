package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.messaging.ConsumerProxy
import akka.amqp.{DeclaredQueue, Delivery, Exchange, Queue}
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.mycelium.core.node.NodeStatus
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/10/13 2:34 PM
 */
abstract class WorkerModule(name: String) extends ConsumerProxy with SLF4JLogging {

  def extract(parsedPayload: JValue): AnyRef

  def handle: PartialFunction[AnyRef, Unit]

  def bindings() = {
    val queue = Queue.default.active()
    val exchange = Exchange.named("worker").active("topic", true)
    Seq(exchange >> queue := name)
  }

  def receive = {
    case del: Delivery =>
      context.self ! extract(parseDelivery(del))
    case _: DeclaredQueue =>
      // pass
    case other: AnyRef =>
      if (handle.isDefinedAt(other)) {
        handle(other)
      } else {
        log.debug("Message not handled: " + other.toString)
      }
  }



}

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.messaging

import akka.amqp.{Delivery, QueueBinding, CreateChannel, AmqpExtension}
import akka.util.Timeout
import scala.concurrent.duration._
import akka.amqp.ChannelActor.{Consumer, Publisher}
import akka.pattern.ask
import concurrent.ExecutionContext
import net.liftweb.json.{JsonParser, DefaultFormats}
import akka.actor.{Actor, ActorRef}
import cz.pechdavid.mycelium.core.module.ModuleRef

/**
 * Proxy allowing message receiving
 *
 * Created: 2/17/13 8:17 PM
 */
trait ConsumerProxy extends ProducerProxy with ModuleRef {
  val conn = AmqpExtension(context.system).connectionActor

  implicit val timeout = Timeout(20 seconds)
  implicit val ec = ExecutionContext.global
  implicit val formats = DefaultFormats

  val cons = conn ? CreateChannel()
  cons.onFailure {
    case t =>
      throw new Exception("Unable to create channel: " + t.toString)
  }
  cons.onSuccess {
    case ch: ActorRef =>
      ch ! Consumer(context.self, true, bindings)
  }

  def bindings(): Seq[QueueBinding]

  /**
   * Delivery parsing
   * @param del
   * @return parsed JSON
   */
  def parseDelivery(del: Delivery) = {
    val inp = new String(del.payload, "utf-8")
    JsonParser.parse(inp)
  }
}

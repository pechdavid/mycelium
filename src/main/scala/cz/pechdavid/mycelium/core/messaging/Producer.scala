/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.messaging

import akka.actor.{ActorRef, Actor}
import akka.amqp.{PublishToExchange, CreateChannel, AmqpExtension}
import akka.util.Timeout
import concurrent.ExecutionContext
import net.liftweb.json.DefaultFormats
import scala.concurrent.duration._
import akka.amqp.ChannelActor.Publisher
import akka.pattern.ask
import akka.event.LoggingAdapter
import akka.event.slf4j.SLF4JLogging

/**
 * Actor with ability to send messages
 *
 * Created: 2/17/13 8:20 PM
 */
class Producer extends Actor with SLF4JLogging {
  val conn = AmqpExtension(context.system).connectionActor

  implicit val timeout = Timeout(20 seconds)
  implicit val ec = ExecutionContext.global
  implicit val formats = DefaultFormats

  val prod = (conn ? CreateChannel()).mapTo[ActorRef]
  prod.onFailure {
    case t =>
      throw new Exception("Unable to create channel")
  }
  prod.onSuccess {
    case ch: ActorRef =>
      ch ! Publisher()
  }

  def receive = {
    case publish: PublishToExchange =>
      prod.foreach {
        _ ! publish
      }

    case other: AnyRef =>
      log.warn("Message not sendable: " + other.toString)
  }
}

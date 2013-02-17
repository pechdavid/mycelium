package cz.pechdavid.mycelium.core.module

import akka.actor.{ActorRef, Actor}
import akka.amqp.{Delivery, QueueBinding, CreateChannel, AmqpExtension}
import akka.util.Timeout
import net.liftweb.json.{DefaultFormats, JsonParser}
import concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.amqp.ChannelActor.{Consumer, Publisher}
import akka.pattern.ask

/**
 * Created: 2/10/13 2:36 PM
 */
abstract class ModuleProxy extends Actor {

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

  val cons = conn ? CreateChannel()
  cons.onFailure {
    case t =>
      throw new Exception("Unable to create channel: " + t.toString)
  }
  cons.onSuccess {
    case ch: ActorRef =>
      ch ! Consumer(context.self, true, bindings)
  }

  case object SchedulerTick

  context.system.scheduler.schedule(0 second, 500 millisecond, context.self, SchedulerTick)

  def bindings(): Seq[QueueBinding]

  def parseDelivery(del: Delivery) = {
    val inp = new String(del.payload, "utf-8")
    JsonParser.parse(inp)
  }




}

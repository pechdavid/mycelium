package cz.pechdavid.mycelium.core.node

import akka.actor.{ActorRef, Actor}
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.duration._
import akka.amqp._
import akka.pattern.ask
import akka.amqp.ChannelActor.Consumer
import akka.amqp.CreateChannel
import akka.amqp.ChannelActor.Publisher
import akka.util.Timeout
import concurrent.ExecutionContext
import net.liftweb.json.{DefaultFormats, JsonParser}
import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/17/13 12:31 PM
 */
class StatusUpdater(node: SystemNode) extends Actor {
  val conn = AmqpExtension(context.system).connectionActor

  implicit val timeout = Timeout(20 seconds)
  implicit val ec = ExecutionContext.global

  val prod = (conn ? CreateChannel()).mapTo[ActorRef]
  prod.onFailure {
    case t =>
      throw new Exception("Unable to create channel")
  }
  prod.onSuccess {
    case ch: ActorRef =>
      ch ! Publisher()
  }

  val queue = Queue.default.active()
  val exchange = Exchange.named("nodeUpdates").active("fanout", true)
  val binding = exchange >> queue

  val cons = conn ? CreateChannel()
  cons.onFailure {
    case t =>
      throw new Exception("Unable to create channel: " + t.toString)
  }
  cons.onSuccess {
    case ch: ActorRef =>
      ch ! Consumer(context.self, true, Seq(binding))
  }

  case object SchedulerTick

  context.system.scheduler.schedule(0 second, 500 millisecond, context.self, SchedulerTick)

  def receive = {
    case SchedulerTick =>
      prod.foreach {
        _ ! PublishToExchange(Message(
              NodeStatus(node.name,
                node.localAvailable,
                node.localRunning), ""), "nodeUpdates", false)
      }

    case del: Delivery =>
      implicit val formats = DefaultFormats
      val inp = new String(del.payload, "utf-8")
      val status = JsonParser.parse(inp).extract[NodeStatus]

      val filtered = node.status.filter { _.name != status.name }
      node.status = filtered ++ Set(status)
  }
}

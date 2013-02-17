package cz.pechdavid.mycelium.core.node

import akka.amqp._
import scala.concurrent.duration._
import cz.pechdavid.mycelium.core.messaging.ConsumerProxy

/**
 * Created: 2/17/13 12:31 PM
 */
class StatusUpdater(node: SystemNode) extends ConsumerProxy {

  case object SchedulerTick
  context.system.scheduler.schedule(0 second, 500 millisecond, context.self, SchedulerTick)

  def receive = {
    case SchedulerTick =>
      producerRef ! PublishToExchange(Message(
          NodeStatus(node.name, node.localAvailable, node.localRunning), ""), "nodeUpdates", false)

    case del: Delivery =>
      val status = parseDelivery(del).extract[NodeStatus]
      val filtered = node.status.filter {
        _.name != status.name
      }
      node.status = filtered ++ Set(status)

  }

  def bindings() = {
    val queue = Queue.default.active()
    val exchange = Exchange.named("nodeUpdates").active("fanout", true)
    Seq(exchange >> queue)
  }
}

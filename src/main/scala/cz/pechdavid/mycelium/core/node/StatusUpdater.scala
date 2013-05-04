package cz.pechdavid.mycelium.core.node

import akka.amqp._
import scala.concurrent.duration._
import cz.pechdavid.mycelium.core.messaging.ConsumerProxy

/**
 * Created: 2/17/13 12:31 PM
 */
class StatusUpdater(node: SystemNode) extends ConsumerProxy {

  val startedAt = System.currentTimeMillis()

  case object SendUpdate

  case object RefreshProxies

  context.system.scheduler.schedule(0 second, 220 millisecond, context.self, SendUpdate)
  context.system.scheduler.schedule(0 second, 400 millisecond, context.self, RefreshProxies)

  def proxyDuplicates(otherNodes: Set[NodeStatus]) {
    for (mod <- node.container.localRunning;
      other <- otherNodes
      if (other.running.contains(mod) && other.name > node.name)) {
      node.replaceWithProxy(mod)
    }
  }

  def startMissing() {
    for (other <- node.container.globalAvailable;
      req <- other.requirements
      if (!node.container.globalRunning.contains(req));
      local = node.container.localAvailable.find {_.name == req}
      if (local.isDefined)) {
      node.startNoRunList(req)
    }
  }

  def notifyAvailableChange(otherNodes: Set[NodeStatus]) {
    for (proxy <- node.container.localProxy) {
      if (node.container.globalRunning.contains(proxy) && node.container.unavailable.contains(proxy)) {
        node.notifyAvailable(proxy)
      } else if (!node.container.globalRunning.contains(proxy)
          && !node.container.unavailable.contains(proxy)
          && System.currentTimeMillis() - startedAt > 1000) {
        if (node.container.localAvailable.find {
          _.name == proxy
        }.isDefined) {
          node.replaceWithModule(proxy)
        } else {
          node.notifyUnavailable(proxy)
        }
      }
    }
  }

  def receive = {
    case SendUpdate =>
      producerRef ! PublishToExchange(Message(
        NodeStatus(node.name, node.container.localAvailable, node.container.localRunning, System.currentTimeMillis()), ""), "nodeUpdates", false)

    case RefreshProxies =>
      node.container.status = node.container.status.filter {
        System.currentTimeMillis() - _.timestampMillis < 1000
      }

      val onlyOthers = node.container.status.filter {
        _.name != node.name
      }

      proxyDuplicates(onlyOthers)
      startMissing()
      notifyAvailableChange(onlyOthers)

    case del: Delivery =>
      val status = parseDelivery(del).extract[NodeStatus]
      val filtered = node.container.status.filter {
        _.name != status.name
      }
      node.container.status = filtered ++ Set(status)

  }

  def bindings() = {
    val queue = Queue.default.active()
    val exchange = Exchange.named("nodeUpdates").active("fanout", true)
    Seq(exchange >> queue)
  }
}

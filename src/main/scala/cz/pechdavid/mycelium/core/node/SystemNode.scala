package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec}
import akka.actor.{Props, ActorSystem}
import akka.amqp.{Connect, AmqpExtension}
import java.util.concurrent.atomic.AtomicReference

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode {
  val system = ActorSystem.create()
  var moduleLaunch = Map.empty[String, Props]
  val connection = AmqpExtension(system).connectionActor
  var localRunning = Set.empty[String]
  var localAvailable = Set.empty[ModuleSpec]

  connection ! Connect

  val status = new AtomicReference(Set.empty[NodeStatus])

  val updater = system.actorOf(Props(new StatusUpdater(status)))

  // FIXME: ping to assign number + name
  // FIXME: local shortcut for delivery
  // FIXME: deadwatch module
  // FIXME: shortcut for status

  def registerProps(map: Map[String, Props]) {
    moduleLaunch = map
  }

  def boot(specs: Set[ModuleSpec], run: List[ModuleProps]) {
    localAvailable ++= specs
  }

  def globalRunning: Set[String] = {
    status.get().map {
      _.running
    }.flatten
  }

  def shutdown() {
    system.shutdown()
  }

  def globalNodes: Set[String] = {
    status.get() map {
      _.name
    }
  }

}

package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module._
import akka.actor.{ActorRef, Props, ActorSystem}
import akka.amqp.{Connect, AmqpExtension}
import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import cz.pechdavid.mycelium.core.module.ModuleProps
import cz.pechdavid.mycelium.core.module.ModuleSpec
import scala.Some

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode {
  val name = NodeName.random()
  val system = ActorSystem.create()
  var moduleLaunch = Map.empty[String, (ModuleProps) => Props]
  val connection = AmqpExtension(system).connectionActor
  var localRunning = Set.empty[String]
  var localAvailable = Set.empty[ModuleSpec]

  connection ! Connect

  var status = Set.empty[NodeStatus]

  val moduleLifecycle = new ModuleLifecycle

  val updater = system.actorOf(Props(new StatusUpdater(this)), "status-updater")
  val supervisor = system.actorOf(Props(new ModuleSupervisor), "supervisor")

  // FIXME: ping to assign number + name
  // FIXME: local shortcut for delivery
  // FIXME: deadwatch module
  // FIXME: shortcut for status
  // FIXME: depency watcher

  def registerProps(map: Map[String, (ModuleProps) => Props]) {
    moduleLaunch = map
  }

  def boot(specs: Set[ModuleSpec], run: List[ModuleProps]) {
    localAvailable ++= specs

    val linear = new DependencyLinearizer(globalAvailable)
    val correctOrder = linear.calculate(globalRunning, run.toList.map {
      _.name
    })

    // FIXME: missing deps

    correctOrder.foreach {
      ordered =>
        val ord = ordered
        val props = run.dropWhile {
          ord != _.name
        }.headOption match {
          case Some(x) => x
        }
        val launch = moduleLaunch.dropWhile {
          ord != _._1
        }.headOption match {
          case Some(x) => x._2
        }

        moduleLifecycle.start(ord, props, launch, supervisor)
    }
  }

  def globalAvailable: Set[ModuleSpec] = {
    status.map {
      _.available
    }.flatten
  }

  def globalRunning: Set[String] = {
    status.map {
      _.running
    }.flatten
  }

  def moduleRef(name: String): ActorRef = {
    ???
  }

  def shutdown() {
    moduleLifecycle.stop(localAvailable, localRunning, supervisor)

    system.shutdown()
  }

  def globalNodes: Set[String] = {
    status map {
      _.name
    }
  }

}

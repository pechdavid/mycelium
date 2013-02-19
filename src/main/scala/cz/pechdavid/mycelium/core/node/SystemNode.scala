package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module._
import akka.actor.{Props, ActorSystem}
import akka.amqp.{Connect, AmqpExtension}
import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import cz.pechdavid.mycelium.core.module.ModuleProps
import cz.pechdavid.mycelium.core.module.ModuleSpec
import scala.Some
import cz.pechdavid.mycelium.core.messaging.Producer

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
  val producer = system.actorOf(Props(new Producer), "producer")

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
    val correctOrder = linear.calculate(globalRunning, run.map {
      _.name
    })

    // FIXME: missing deps

    // FIXME: only required

    val runNames = moduleLaunch.keys.toSet

    correctOrder.foreach {
      ordered =>
        if (runNames.contains(ordered)) {
          val lookup = lookupValues(run, ordered)

          moduleLifecycle.create(ordered, lookup._2, lookup._1, supervisor)
        } else {
          moduleLifecycle.startProxy(ordered, supervisor)
        }
    }

    correctOrder.foreach {
      ordered =>
        if (runNames.contains(ordered)) {
          moduleLifecycle.start(ordered, supervisor)

          localRunning += ordered
        } else {
          // proxy - pass
        }
    }
  }


  def lookupValues(run: List[ModuleProps], ord: String): ((ModuleProps) => Props, ModuleProps) = {
    val props = run.dropWhile {
      ord != _.name
    }.headOption match {
      case Some(x) => x
      case None => ModuleProps(ord, None)
    }
    val launch = moduleLaunch.dropWhile {
      ord != _._1
    }.headOption match {
      case Some(x) => x._2
      case None => throw new RuntimeException
    }
    (launch, props)
  }

  def globalAvailable: Set[ModuleSpec] = {
    status.map {
      _.available
    }.flatten ++ localAvailable
  }

  def globalRunning: Set[String] = {
    status.map {
      _.running
    }.flatten ++ localRunning
  }

  def moduleRef(name: String) = {
    system.actorFor("/user/supervisor/" + name)
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

package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module._
import akka.actor.{Props, ActorSystem}
import akka.amqp.{Connect, AmqpExtension}
import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import cz.pechdavid.mycelium.core.module.ModuleProps
import cz.pechdavid.mycelium.core.module.ModuleSpec
import scala.Some
import cz.pechdavid.mycelium.core.messaging.Producer
import scala.concurrent.duration._
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode(moduleLaunch: Map[String, (ModuleProps) => Props] = Map.empty) extends SLF4JLogging {
  val name = NodeName.random()
  val system = ActorSystem.create("mycelium")
  val connection = AmqpExtension(system).connectionActor

  connection ! Connect

  val container = new ModuleContainer

  val updater = system.actorOf(Props(new StatusUpdater(this)), "status-updater")
  val supervisor = system.actorOf(Props(new ModuleSupervisor), "supervisor")
  val producer = system.actorOf(Props(new Producer), "producer")
  val lifecycle = new ModuleLifecycle(supervisor)

  // FIXME: ping to assign number + name
  // FIXME: local shortcut for delivery
  // FIXME: deadwatch module
  // FIXME: shortcut for status
  // FIXME: depency watcher

  def boot(specs: Set[ModuleSpec], run: List[ModuleProps], appendToRunList: Boolean = false) {
    container.localAvailable ++= specs

    val completeRunList = container.runArgs ++ run
    if (appendToRunList) {
      container.runArgs = completeRunList
    }

    val linear = new DependencyLinearizer(container.globalAvailable)
    val correctOrder = linear.calculate(container.globalRunning, completeRunList.map {
      _.name
    })

    log.info("Boot sequence: " + correctOrder)

    // FIXME: missing deps

    // FIXME: only required

    val runNames = moduleLaunch.keys.toSet

    correctOrder.foreach {
      ordered =>
        if (runNames.contains(ordered)) {
          val lookup = lookupValues(completeRunList, ordered)

          lifecycle.create(ordered, lookup._2, lookup._1)
        } else {
          lifecycle.startProxy(ordered)
        }
    }

    correctOrder.foreach {
      ordered =>
        if (runNames.contains(ordered)) {
          lifecycle.start(ordered)

          container.localRunning += ordered
        } else {
          container.localProxy += ordered
        }
    }
  }

  def replaceWithProxy(mod: String) {
    lifecycle.stopSilently(mod)
    lifecycle.startProxy(mod)
    container.localProxy += mod
    container.localRunning -= mod
  }

  def startNoRunList(mod: String) {
    boot(Set.empty, List(ModuleProps(mod)), false)
  }

  def notifyAvailable(proxy: String) {
    container.unavailable -= proxy
    lifecycle.notifyAvailable(proxy, container.directRequiring(proxy))
  }

  def notifyUnavailable(proxy: String) {
    container.unavailable += proxy
    lifecycle.notifyUnavailable(proxy, container.directRequiring(proxy))
  }

  def replaceWithModule(proxy: String) {
    lifecycle.stopSilently(proxy)
    boot(Set.empty, List(ModuleProps(proxy)), false)
  }


  private def lookupValues(run: List[ModuleProps], ord: String): ((ModuleProps) => Props, ModuleProps) = {
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

  def moduleRef(name: String) = {
    system.actorFor("/user/supervisor/" + name)
  }

  def shutdown() {
    lifecycle.stop(container.localAvailable, container.localRunning)

    system.shutdown()

    system.awaitTermination(1 minute)
  }
}

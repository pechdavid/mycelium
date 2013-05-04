/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module._
import akka.actor.{Props, ActorSystem}
import akka.amqp.{Connect, AmqpExtension}
import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import cz.pechdavid.mycelium.core.module.ModuleSpec
import scala.Some
import cz.pechdavid.mycelium.core.messaging.Producer
import scala.concurrent.duration._
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode(launchPatterns: Map[String, (ModuleSpec) => Props] = Map.empty) extends SLF4JLogging {
  val name = NodeName.random()
  val system = ActorSystem.create("mycelium")
  val connection = AmqpExtension(system).connectionActor

  connection ! Connect

  val container = new ModuleContainer

  val updater = system.actorOf(Props(new StatusUpdater(this)), "status-updater")
  val supervisor = system.actorOf(Props(new ModuleSupervisor), "supervisor")
  val producer = system.actorOf(Props(new Producer), "producer")
  val lifecycle = new ModuleLifecycle(supervisor)

  def boot(specs: Set[ModuleSpec], run: List[String], appendToRunList: Boolean = true) {
    container.localAvailable ++= specs

    val mapSpec = (container.localAvailable.map {
      s =>
        s.name -> s
    }).toMap

    val completeRunList = container.localRunning.toList ++ run
    val linear = new DependencyLinearizer(container.globalAvailable)
    val correctOrder = linear.calculate(container.globalRunning, completeRunList)

    if (appendToRunList) {
      container.localRunning = completeRunList.toSet
    }

    log.info("Boot sequence: " + correctOrder + ", from run list: " + completeRunList)

    correctOrder.foreach {
      ordered =>
        launchPatterns.get(mapSpec(ordered).launchPattern) match {
          case Some(pattern) =>
            lifecycle.create(ordered, pattern(mapSpec(ordered)))

          case None =>
            lifecycle.startProxy(ordered)
        }
    }

    correctOrder.foreach {
      ordered =>
        launchPatterns.get(mapSpec(ordered).launchPattern) match {
          case Some(pattern) =>
            lifecycle.start(ordered)

            container.localRunning += ordered

          case None =>
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
    boot(Set.empty, List(mod), false)
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
    boot(Set.empty, List(proxy), false)
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

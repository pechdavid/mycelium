/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import akka.actor.{Props, ActorRef}

/**
 * Lifecycle control
 *
 * Created: 2/17/13 6:30 PM
 */
class ModuleLifecycle(supervisor: ActorRef) {
  /**
   * Available module!
   * @param module
   * @param requiring
   */
  def notifyAvailable(module: String, requiring: Set[String]) {

    requiring.foreach {
      target =>
        supervisor ! Forward(target, DependencyOnline(module))
    }
  }

  /**
   * Module offline
   * @param module
   * @param requiring
   */
  def notifyUnavailable(module: String, requiring: Set[String]) {

    requiring.foreach {
      target =>
        supervisor ! Forward(target, DependencyNotOnline(module))
    }
  }

  def stopSilently(module: String) {
    supervisor ! StopSilentlyModule(module)
  }

  /**
   * Start proxy for remote module
   * @param name
   */
  def startProxy(name: String) {
    supervisor ! StartNewModule(name, Props(new ForwardModule(name)))
  }

  /**
   * Create new module
   */
  def create(name: String, props: Props) {
    supervisor ! StartNewModule(name, props)

    supervisor ! Forward(name, PostInitialize)
  }

  /**
   * Starting...
   * @param name
   */
  def start(name: String) {
    supervisor ! Forward(name, StartModule)
  }

  /**
   * Stop and wait
   * @param localAvailable
   * @param localRunning
   */
  def stop(localAvailable: Set[ModuleSpec], localRunning: Set[String]) {
    val linear = new DependencyLinearizer(localAvailable)

    val terminateList = linear.calculate(Set.empty, localRunning.toList).reverse

    terminateList.foreach {
      supervisor ! Forward(_, StopModule)
    }

    Thread.sleep(500)

    terminateList.foreach {
      supervisor ! Forward(_, PostStop)
    }

    Thread.sleep(500)
  }
}

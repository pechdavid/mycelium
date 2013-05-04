package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import akka.actor.{Props, ActorRef}

/**
 * Created: 2/17/13 6:30 PM
 */
class ModuleLifecycle(supervisor: ActorRef) {
  def notifyAvailable(module: String, requiring: Set[String]) {

    requiring.foreach {
      target =>
        supervisor ! Forward(target, DependencyOnline(module))
    }
  }

  def notifyUnavailable(module: String, requiring: Set[String]) {

    requiring.foreach {
      target =>
        supervisor ! Forward(target, DependencyNotOnline(module))
    }
  }

  def stopSilently(module: String) {
    supervisor ! StopSilentlyModule(module)
  }


  def startProxy(name: String) {
    supervisor ! StartNewModule(name, Props(new ForwardModule(name)))
  }

  def create(name: String, props: Props) {
    supervisor ! StartNewModule(name, props)

    supervisor ! Forward(name, PostInitialize)
  }

  def start(name: String) {
    supervisor ! Forward(name, StartModule)
  }

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

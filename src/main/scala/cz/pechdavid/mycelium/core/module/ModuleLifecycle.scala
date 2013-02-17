package cz.pechdavid.mycelium.core.module

import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import akka.actor.{Props, ActorRef}

/**
 * Created: 2/17/13 6:30 PM
 */
class ModuleLifecycle {
  def create(name: String, args: ModuleProps, props: (ModuleProps) => Props, moduleSupervisor: ActorRef) {

    moduleSupervisor ! StartNewModule(name, props(args))

    moduleSupervisor ! Forward(name, PostInitialize)
  }

  def start(name: String, moduleSupervisor: ActorRef) {
    // FIXME check deps...
    moduleSupervisor ! Forward(name, StartModule)
  }

  def stop(localAvailable: Set[ModuleSpec], localRunning: Set[String], moduleSupervisor: ActorRef) {
    val linear = new DependencyLinearizer(localAvailable)

    val terminateList = linear.calculate(Set.empty, localRunning.toList).reverse

    terminateList.foreach {
      moduleSupervisor ! Forward(_, StopModule)
    }

    Thread.sleep(500)

    terminateList.foreach {
      moduleSupervisor ! Forward(_, PostStop)
    }

    Thread.sleep(500)
  }
}

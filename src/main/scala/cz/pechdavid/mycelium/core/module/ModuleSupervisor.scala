/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.module

import akka.event.slf4j.SLF4JLogging


/**
 * Supervisor strategy for all modules
 *
 * Created: 2/17/13 6:34 PM
 */
class ModuleSupervisor extends ModuleRef with SLF4JLogging {

  def receive = {
    case StartNewModule(name, props) =>
      log.debug("Registering module: " + name + ", sender: " + sender.toString())
      if (context.child(name).isDefined) {
        log.error("Child already defined: " + name)
      } else {
        context.actorOf(props, name)
      }

    case Forward(name, msg) =>
      moduleRef(name) ! msg

    case StopSilentlyModule(name) =>
      context.stop(moduleRef(name))

      Thread.sleep(50)
  }
}

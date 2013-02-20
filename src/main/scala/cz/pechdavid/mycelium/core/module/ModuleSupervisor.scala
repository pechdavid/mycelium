package cz.pechdavid.mycelium.core.module


/**
 * Created: 2/17/13 6:34 PM
 */
class ModuleSupervisor extends ModuleRef {

  // FIXME: supervisor strategy

  def receive = {
    case StartNewModule(name, props) =>
      context.actorOf(props, name)

    case Forward(name, msg) =>
      moduleRef(name) ! msg

    case StopSilentlyModule(name) =>
      context.stop(moduleRef(name))

      Thread.sleep(50)
  }
}

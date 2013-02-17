package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec}
import akka.actor.{Props, ActorSystem}

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode {
  def globalNodes: Set[Long] = ???

  val system = ActorSystem.create()

  // FIXME: ping to assign number + name
  // FIXME: local shortcut for delivery


  def registerProps(map: Map[String, Props]) {
    ???
  }


  def boot(specs: Set[ModuleSpec], run: List[ModuleProps]) {
    ???
  }

  def globalRunning: Set[String] = ???

  def localRunning: Set[String] = ???

  def shutdown() {
    system.shutdown()
  }
}

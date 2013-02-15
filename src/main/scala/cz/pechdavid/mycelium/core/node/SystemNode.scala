package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module.{ModuleProps, ModuleSpec}
import akka.actor.{Props, ActorSystem}

/**
 * Created: 2/15/13 5:53 PM
 */
class SystemNode(priority: Int) {
  def registerProps(map: Map[String, Props]) {
    ???
  }


  val system = ActorSystem.create()

  def boot(specs: Set[ModuleSpec], run: List[ModuleProps]) {
    ???
  }

  def running: Set[String] = ???
}

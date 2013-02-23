package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/19/13 11:45 PM
 */
class ModuleContainer {
  var localRunning = Set.empty[String]
  var localAvailable = Set.empty[ModuleSpec]
  var localProxy = Set.empty[String]
  var unavailable = Set.empty[String]

  var status = Set.empty[NodeStatus]

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

  def globalNodes: Set[String] = {
    status map {
      _.name
    }
  }

  def directRequiring(dep: String): Set[String] = {
    localAvailable.filter {
      each =>
        each.requirements.contains(dep) && localRunning.contains(each.name)
    }.map {
      _.name
    }
  }

}

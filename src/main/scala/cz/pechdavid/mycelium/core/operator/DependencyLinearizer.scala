package cz.pechdavid.mycelium.core.operator

import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/15/13 5:58 PM
 */
class DependencyLinearizer(availableModules: Set[ModuleSpec]) {
  def calculate(running: Set[String], desired: List[String]): List[String] = {
    // FIXME: HERE
    desired
  }
}

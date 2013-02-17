package cz.pechdavid.mycelium.core.node

import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/17/13 12:34 PM
 */
case class NodeStatus(name: String, available: Set[ModuleSpec], running: Set[String])

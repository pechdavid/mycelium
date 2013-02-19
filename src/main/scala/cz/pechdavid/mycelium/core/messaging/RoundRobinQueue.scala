package cz.pechdavid.mycelium.core.messaging

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 2/19/13 11:13 PM
 */
class RoundRobinQueue(name: String, targets: List[String]) extends WorkerModule(name) {
  def extract(parsedPayload: JValue) = ???

  def handle = ???
}

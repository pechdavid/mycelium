package cz.pechdavid.webweaver.graph

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 2/24/13 5:49 PM
 */
class GraphProjection(name: String) extends WorkerModule(name) {
  def extract(parsedPayload: JValue) = ???

  def handle = ???
}

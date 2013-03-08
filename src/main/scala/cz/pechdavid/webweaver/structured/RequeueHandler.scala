package cz.pechdavid.webweaver.structured

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 3/8/13 8:34 PM
 */
class RequeueHandler(name: String) extends WorkerModule(name) {
  def extract(parsedPayload: JValue) = ???

  def handle = ???
}

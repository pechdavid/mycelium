package cz.pechdavid.webweaver.raw

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 2/24/13 5:48 PM
 */
class RawContentProjection() extends WorkerModule("rawContentProjection") {
  def extract(parsedPayload: JValue) = ???

  def handle = ???
}

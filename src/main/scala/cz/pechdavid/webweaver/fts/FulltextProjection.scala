package cz.pechdavid.webweaver.fts

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Created: 2/24/13 5:45 PM
 */
class FulltextProjection extends WorkerModule("fulltextProjection") {
  def extract(parsedPayload: JValue) = ???

  def handle = ???
}

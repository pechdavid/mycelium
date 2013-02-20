package cz.pechdavid.mycelium.core.messaging

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import net.liftweb.json.DefaultFormats

/**
 * Created: 2/19/13 11:13 PM
 */
class RoundRobinQueue[A <: AnyRef : Manifest](name: String, targets: List[String]) extends WorkerModule(name) {

  require(!targets.isEmpty)

  var i = 0

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract(DefaultFormats, manifest[A])
  }

  def handle = {
    case msg: AnyRef =>
      moduleRef(targets(i)) ! msg

      i = if (i >= targets.size - 1) {
        0
      } else {
        i + 1
      }
  }
}

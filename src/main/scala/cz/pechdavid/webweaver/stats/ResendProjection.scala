/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.webweaver.structured.ParsedHtml

/**
 * Simple relay projection
 *
 * Created: 4/13/13 11:13 PM
 */
class ResendProjection extends WorkerModule("resendProjection") {
  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[ParsedHtml]
  }

  def handle = {
    case req: ParsedHtml =>
      if (!req.links.isEmpty) {
        moduleRef("commandBus") ! AddNewLink(req.url, req.links)
      }
  }
}

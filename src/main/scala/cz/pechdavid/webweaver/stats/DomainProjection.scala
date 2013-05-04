/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue

/**
 * Projection to domain
 *
 * Created: 4/14/13 12:12 AM
 */
class DomainProjection extends WorkerModule("domainProjection") {
  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[NewLinkAdded]
  }

  def handle = {
    case req: NewLinkAdded =>
      DomainMemoryStore.increment(req.domainHost, req.linkHost)
  }
}

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.graph

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.webweaver.structured.ParsedHtml

/**
 * Export to neo4j
 *
 * Created: 2/24/13 5:49 PM
 */
class GraphProjection extends WorkerModule("graphProjection") {
  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[ParsedHtml]
  }

  def handle = {
    case doc: ParsedHtml =>
      Neo4jDb.maybeNodeAndLinks(doc.url, doc.links)
  }
}

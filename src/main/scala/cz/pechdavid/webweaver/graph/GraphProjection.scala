package cz.pechdavid.webweaver.graph

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.webweaver.structured.ParsedHtml

/**
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

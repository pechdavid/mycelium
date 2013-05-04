/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.webweaver.structured.ParsedHtml
import java.net.{MalformedURLException, URL}

/**
 * Adds a new URL back to the queue
 *
 * Created: 4/11/13 5:39 PM
 */
class RequeueProjection(allowedHosts: Option[Set[String]]) extends WorkerModule("requeueProjection") {
  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[ParsedHtml]
  }

  def handle = {
    case html: ParsedHtml =>
      html.links.map {
        url =>
          try {
            Some(new URL(new URL(html.url), url))
          } catch {
            case ex: MalformedURLException => None
          }
      }.filter {
        _.isDefined
      }.map {
        _.get
      }.filter {
        u => AddToQueue.isValid(u.toString)
      }.filter {
        u => allowedHosts.isEmpty || allowedHosts.get.contains(u.getHost)
      }.foreach {
        u =>
          moduleRef("queue") ! AddToQueue(u.toString)
      }
  }
}

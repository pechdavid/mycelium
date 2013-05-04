/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.extension.http

import cz.pechdavid.mycelium.core.module.{StopModule, StartModule, WorkerModule}
import spray.routing.SimpleRoutingApp
import net.liftweb.json.JsonAST.JValue
import spray.routing.Route
import akka.actor.PoisonPill

/**
 * Basic web server module launcher
 *
 * Created: 3/10/13 11:19 AM
 */
class WebServerModule(name: String, interface: String, port: Int)(routing: RoutingRules) extends WorkerModule(name) with SimpleRoutingApp {
  def extract(parsedPayload: JValue) = {
    parsedPayload
  }

  def handle = {
    case StartModule =>
      startServer(interface, port)(routing.routing(moduleRef("fulltextProjection"), moduleRef("queue")))

    case StopModule =>
      context.child("http-server") match {
        case Some(act) =>
          act ! PoisonPill
        case None =>
          // pass
      }
  }
}

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.extension.http

import akka.actor.{ActorRef, ActorRefFactory}
import spray.routing.Route
import cz.pechdavid.webweaver.raw.{RawContentTrl, RawContentProjection}

/**
 * Routing interface
 *
 * Created: 3/11/13 7:52 PM
 */
trait RoutingRules {

  def routing(ftsModule: ActorRef, queue: ActorRef)(implicit actorRefFactory: ActorRefFactory): Route

}

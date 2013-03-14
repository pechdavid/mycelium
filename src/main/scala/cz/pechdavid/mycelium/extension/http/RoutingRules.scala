package cz.pechdavid.mycelium.extension.http

import akka.actor.{ActorRef, ActorRefFactory}
import spray.routing.Route

/**
 * Created: 3/11/13 7:52 PM
 */
trait RoutingRules {

  def routing(ftsModule: ActorRef)(implicit actorRefFactory: ActorRefFactory): Route

}

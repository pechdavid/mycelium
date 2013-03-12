package cz.pechdavid.mycelium.extension.http

import akka.actor.ActorRefFactory
import spray.routing.Route

/**
 * Created: 3/11/13 7:52 PM
 */
trait RoutingRules {

  def routing(implicit actorRefFactory: ActorRefFactory): Route

}

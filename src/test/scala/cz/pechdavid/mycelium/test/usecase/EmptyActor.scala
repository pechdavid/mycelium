/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.usecase

import akka.actor.Actor
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 11:44 PM
 */
@RunWith(classOf[JUnitRunner])
class EmptyActor extends Actor {
  def receive = {
    case x: AnyRef =>
    // pass
  }
}



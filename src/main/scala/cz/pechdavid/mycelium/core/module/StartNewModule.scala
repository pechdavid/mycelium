/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.module

import akka.actor.Props

/**
 * Start Command
 *
 * Created: 2/17/13 6:44 PM
 */
case class StartNewModule(name: String, props: Props)

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.event.Event

/**
 * Downloaded Command
 *
 * Created: 2/22/13 7:54 PM
 */
case class Downloaded(url: String, cont: String) extends Event

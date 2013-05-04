/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.event.Event

/**
 * Event fro adding a new link
 *
 * Created: 4/13/13 10:52 PM
 */
case class NewLinkAdded(domainHost: String, linkHost: String) extends Event

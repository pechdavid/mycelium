package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.event.Event

/**
 * Created: 4/13/13 10:52 PM
 */
case class NewLinkAdded(domainHost: String, linkHost: String) extends Event

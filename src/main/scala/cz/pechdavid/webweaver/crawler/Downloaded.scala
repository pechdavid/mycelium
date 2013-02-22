package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.event.Event

/**
 * Created: 2/22/13 7:54 PM
 */
case class Downloaded(url: String, cont: String) extends Event

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.command

import cz.pechdavid.mycelium.core.event.Event

/**
 * Interface for handling commands and returning list of events
 *
 * Created: 2/16/13 11:20 PM
 */
trait CommandHandler {
  def handle: PartialFunction[AnyRef, List[Event]]
}

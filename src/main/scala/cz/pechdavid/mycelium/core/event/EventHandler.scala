/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.event

import cz.pechdavid.mycelium.core.projection.NotifyProjections

/**
 * Event handler interface
 *
 * Created: 2/16/13 11:20 PM
 */
trait EventHandler {
  def handle: PartialFunction[Event, List[NotifyProjections]]
}

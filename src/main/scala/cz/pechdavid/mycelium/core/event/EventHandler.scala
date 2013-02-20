package cz.pechdavid.mycelium.core.event

import cz.pechdavid.mycelium.core.projection.NotifyProjections

/**
 * Created: 2/16/13 11:20 PM
 */
trait EventHandler {
  def handle: PartialFunction[Event, List[NotifyProjections]]
}

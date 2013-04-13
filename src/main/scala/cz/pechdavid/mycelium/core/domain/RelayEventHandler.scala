package cz.pechdavid.mycelium.core.domain

import cz.pechdavid.mycelium.core.event.EventHandler
import cz.pechdavid.mycelium.core.projection.NotifyProjections

/**
 * Created: 4/14/13 12:04 AM
 */
class RelayEventHandler(notify: Set[String]) extends EventHandler {
  def handle = {
    case everything =>
      List(NotifyProjections(notify, everything))
  }
}

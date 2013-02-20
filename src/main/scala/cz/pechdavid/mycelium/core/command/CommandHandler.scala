package cz.pechdavid.mycelium.core.command

import cz.pechdavid.mycelium.core.event.Event

/**
 * Created: 2/16/13 11:20 PM
 */
trait CommandHandler {
  def handle: PartialFunction[AnyRef, List[Event]]
}

package cz.pechdavid.mycelium.core.domain

import cz.pechdavid.mycelium.core.event.Event
import scala.collection.mutable

/**
 * Created: 4/13/13 9:41 PM
 */
trait AggregateRoot {
  def apply(event: Event)

  val unsavedEvents = mutable.MutableList.empty[Event]

  def applyUnsaved(event: Event) {
    unsavedEvents += event
    apply(event)
  }

  def retrieveResetEvents = {
    val lst = unsavedEvents.toList

    unsavedEvents.clear()

    lst
  }
}

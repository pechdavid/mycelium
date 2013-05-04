/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.domain

import cz.pechdavid.mycelium.core.event.Event
import scala.collection.mutable

/**
 * Aggregate root basics.
 *
 * Created: 4/13/13 9:41 PM
 */
trait AggregateRoot {
  def apply(event: Event)

  /**
   * Store currently unsaved events
   */
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

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.domain

import cz.pechdavid.mycelium.core.event.Event

/**
 * Simple events store with basic ops.
 * Created: 4/13/13 10:05 PM
 */
trait EventStore {

  def save(root: AggregateRoot)

  /**
   * List aggregate roots
   * @return roots
   */
  def listRoots: Set[String]

  /**
   * Load all events for certain root
   * @return all events
   */
  def loadForRoot: List[Event]
}

package cz.pechdavid.mycelium.core.domain

import cz.pechdavid.mycelium.core.event.Event

/**
 * Created: 4/13/13 10:05 PM
 */
trait EventStore {

  def save(root: AggregateRoot)

  def listRoots: Set[String]

  def loadForRoot: List[Event]
}

package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.event.Event
import scala.collection.mutable

/**
 * Created: 4/13/13 10:54 PM
 */
object DomainHostRepository {
  val singleton = new DomainHostRepository
}

class DomainHostRepository {

  val repository = mutable.Map.empty[String, mutable.MutableList[Event]]

  def loadCreateByHost(host: String) = {
    if (!repository.contains(host)) {
      repository(host) = mutable.MutableList.empty[Event]
    }

    val root = new DomainHost(host)

    for (ev <- repository(host)) {
      root(ev)
    }

    root
  }

  def save(root: DomainHost) = {
    val unsaved = root.retrieveResetEvents

    repository(root.host) ++= unsaved

    unsaved
  }
}

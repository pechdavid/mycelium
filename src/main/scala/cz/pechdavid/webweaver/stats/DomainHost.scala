/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

import cz.pechdavid.mycelium.core.event.Event
import cz.pechdavid.mycelium.core.domain.AggregateRoot
import scala.collection.mutable
import java.net.URL

/**
 * Aggregate root for domain example
 *
 * Created: 4/13/13 9:39 PM
 */
class DomainHost(val host: String) extends AggregateRoot {

  val links = mutable.Map.empty[String, Long]

  def apply(event: Event) {
    event match {
      case NewLinkAdded(_, u) =>

        if (!links.contains(u)) {
          links(u) = 0
        }

        links(u) += 1
    }
  }

  def incrementForHost(otherHost: URL) {
    applyUnsaved(NewLinkAdded(host, otherHost.getHost))
  }
}

package cz.pechdavid.webweaver.stats

import scala.collection.mutable

/**
 * Created: 4/14/13 12:13 AM
 */
object DomainMemoryStore {
  def increment(left: String, right: String) {
    if (!storage.contains(left)) {
      storage(left) = mutable.Map.empty[String, Long]
    }

    val map = storage(left)

    if (!map.contains(right)) {
      map(right) = 0
    }

    map(right) += 1
  }

  val storage = mutable.Map.empty[String, mutable.Map[String, Long]]

}

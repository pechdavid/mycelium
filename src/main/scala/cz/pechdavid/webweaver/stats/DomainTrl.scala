/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

import scala.util.Random

/**
 * Load data from domain
 *
 * Created: 4/14/13 12:18 AM
 */
class DomainTrl {
  val colors = List("#00FFCC", "#FFDD89", "#957244", "#F26223", "#99FF66")

  def selectRandom = {
    Random.shuffle(DomainMemoryStore.storage.keys).take(colors.size).toList
  }

  def statRandomDomains = {
    val selected = selectRandom

    selected.zip(colors).map {
      each =>
        val mm = DomainMemoryStore.storage(each._1)
        val matrix = selected.map {
          sel =>
            if (mm.contains(sel)) {
              mm(sel)
            } else {
              0L
            }
        }.toList

        DomainStats(each._1, each._2, matrix)
    }
  }

}

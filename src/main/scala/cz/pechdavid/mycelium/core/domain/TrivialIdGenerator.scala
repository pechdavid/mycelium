/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.domain

import java.util.UUID

/**
 * Custom ID generator using UUID
 *
 * Created: 4/13/13 9:58 PM
 */
object TrivialIdGenerator {
  def newId = {
    UUID.randomUUID().toString
  }
}

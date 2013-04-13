package cz.pechdavid.mycelium.core.domain

import java.util.UUID

/**
 * Created: 4/13/13 9:58 PM
 */
object TrivialIdGenerator {
  def newId = {
    UUID.randomUUID().toString
  }
}

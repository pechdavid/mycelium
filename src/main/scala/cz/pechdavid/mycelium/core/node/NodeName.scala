/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.node

import java.security.MessageDigest

/**
 * Name generator
 *
 * Created: 2/17/13 3:02 PM
 */
object NodeName {
  def random() = {
    val rand = System.nanoTime().toString

    MessageDigest.getInstance("SHA-1")
      .digest(rand.getBytes).map(_ & 0xFF).map(_.toHexString).mkString.substring(0, 8)
  }
}

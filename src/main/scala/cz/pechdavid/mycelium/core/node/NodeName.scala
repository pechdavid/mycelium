package cz.pechdavid.mycelium.core.node

import java.security.MessageDigest

/**
 * Created: 2/17/13 3:02 PM
 */
object NodeName {
  def random() = {
    val rand = System.nanoTime().toString

    // FIXME: londer...
    MessageDigest.getInstance("SHA-1")
      .digest(rand.getBytes).map(_ & 0xFF).map(_.toHexString).mkString.substring(0, 8)
  }
}

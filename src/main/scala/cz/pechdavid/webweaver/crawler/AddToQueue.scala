/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.crawler

import java.net.URL
import util.Try

/**
 * Command for adding a new URL to the queue
 *
 * Created: 2/22/13 7:47 PM
 */
case class AddToQueue(url: String)

object AddToQueue {
  def isValid(url: String) = {
    Try(new URL(url)).isSuccess || url.startsWith("http")
  }
}

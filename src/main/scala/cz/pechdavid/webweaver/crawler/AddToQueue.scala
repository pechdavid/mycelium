package cz.pechdavid.webweaver.crawler

import java.net.URL
import util.Try

/**
 * Created: 2/22/13 7:47 PM
 */
case class AddToQueue(url: String)

object AddToQueue {
  def isValid(url: String) = {
    Try(new URL(url)).isSuccess || url.startsWith("http")
  }
}

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.webweaver.crawler.{Downloaded, DownloadHandler, WebWeaver}
import java.util.concurrent.LinkedBlockingDeque
import java.util.Deque
import cz.pechdavid.mycelium.core.event.EventHandler
import akka.actor.Props
import cz.pechdavid.webweaver.crawler.SingleUrlQueue
import cz.pechdavid.mycelium.core.module.ModuleSpec

/**
 * Created: 2/22/13 5:59 PM
 */
@RunWith(classOf[JUnitRunner])
class SimpleDownload extends FlatSpec with ShouldMatchers {

  class TrapEventHandler(queue: Deque[Downloaded]) extends EventHandler {
    def handle = {
      case d: Downloaded =>
        queue.add(d)
        List.empty
    }
  }

  it should "Download basic" in {

    val queue = new LinkedBlockingDeque[String]()
    queue.add("www.root.cz")

    val targetQueue = new LinkedBlockingDeque[Downloaded]()

    val urlQueue = (_: ModuleSpec) => Props(new SingleUrlQueue(queue))

    new WebWeaver(Map("queue" -> urlQueue), List(ModuleSpec("queue")),
      List(new DownloadHandler),
      List(new TrapEventHandler(targetQueue))
    )

    Thread.sleep(10000)

    targetQueue.size() should be(1)
    targetQueue.getFirst.cont.size should be > (0)
  }
}

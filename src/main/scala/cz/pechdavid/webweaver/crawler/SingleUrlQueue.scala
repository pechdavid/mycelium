package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.module.{StartModule, WorkerModule}
import java.util
import net.liftweb.json.JsonAST.JValue
import scala.concurrent.duration._
import akka.event.slf4j.SLF4JLogging

/**
 * Created: 2/22/13 7:28 PM
 */
class SingleUrlQueue(queue: util.Deque[String]) extends WorkerModule("queue") with SLF4JLogging {

  case object PushUrl

  def handle = {
    case StartModule =>
      context.system.scheduler.schedule(2 seconds, 1 second, context.self, PushUrl)

    case AddToQueue(url) =>
      queue.add(url)
    case PushUrl =>
      if (!queue.isEmpty) {
        val url = queue.pop()
        log.debug("Sending download request: " + url)
        moduleRef("commandBus") ! DownloadUrl(url)
      }
  }

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[AddToQueue]
  }
}


package cz.pechdavid.webweaver.crawler

import cz.pechdavid.mycelium.core.module.{StartModule, WorkerModule}
import net.liftweb.json.JsonAST.JValue
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import com.mongodb.casbah.commons.MongoDBObject
import java.util.Date
import scala.concurrent.duration._
import com.mongodb.casbah.Imports._

/**
 * Created: 4/2/13 9:36 PM
 */
class PersistentQueue(connection: ConnectionParams) extends WorkerModule("queue") {

  val queue = connection.collection("queue")

  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[AddToQueue]
  }

  def handle = {
    case StartModule =>
      context.system.scheduler.schedule(2 seconds, 1 second, context.self, PushUrl)

    case AddToQueue(url) =>
      val dbo = MongoDBObject("url" -> url, "at" -> new Date)

      queue.insert(dbo)

    case PushUrl =>
      queue.findAndRemove(MongoDBObject()) match {
        case Some(randomPick) =>
          val url = randomPick.as[String]("url")

          log.debug("Sending download request: " + url)
          moduleRef("commandBus") ! DownloadUrl(url)
        case None =>
          // pass
      }

    case QueuePeek =>
      sender ! queue.find().limit(10).map { e => e.as[String]("url") }
  }
}

case object PushUrl
case object QueuePeek

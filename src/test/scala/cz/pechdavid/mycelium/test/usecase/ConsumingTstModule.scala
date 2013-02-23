package cz.pechdavid.mycelium.test.usecase

import akka.testkit.TestActor
import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.JValue
import java.util

/**
 * Created: 2/19/13 11:15 PM
 */
class ConsumingTstModule(name: String, queue: util.Deque[TestActor.Message]) extends WorkerModule(name) {
  def extract(parsedPayload: JValue) = {
    parsedPayload.extract[TstMessage]
  }

  def handle = {
    case msg: AnyRef =>
      queue.add(TestActor.RealMessage(msg, sender))
  }
}


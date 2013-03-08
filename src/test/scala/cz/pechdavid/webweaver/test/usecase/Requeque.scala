package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.webweaver.crawler.{DownloadHandler, WebWeaver, SingleUrlQueue, Downloaded}
import cz.pechdavid.mycelium.core.module.{StartModule, PostInitialize, ModuleSpec}
import akka.actor.Props
import akka.testkit.TestActor
import cz.pechdavid.mycelium.test.usecase.ConsumingTstModule
import cz.pechdavid.webweaver.structured.{RequeueHandler, ParsedHtml, ParserEventHandler}

/**
 * Created: 3/8/13 8:10 PM
 */
@RunWith(classOf[JUnitRunner])
class Requeque extends FlatSpec with ShouldMatchers {

  it should "Send new urls back to queue" in {
    val queue = new LinkedBlockingDeque[String]()
    queue.add("www.root.cz")

    val urlQueue = (_: ModuleSpec) => Props(new SingleUrlQueue(queue))

    new WebWeaver(Map("queue" -> urlQueue, "requeue" -> ((_: ModuleSpec) => Props(new RequeueHandler("requeue")))),
      List(ModuleSpec("queue"), ModuleSpec("myProjection")),
      List(new DownloadHandler),
      List(new ParserEventHandler(Set("requeue")))
    )

    Thread.sleep(15000)

    queue.isEmpty should be (false)
    queue.getFirst should not be ("www.root.cz")
  }


}
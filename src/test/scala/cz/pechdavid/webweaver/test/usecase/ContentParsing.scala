package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.webweaver.crawler.{DownloadHandler, WebWeaver, SingleUrlQueue, Downloaded}
import akka.actor.Props
import cz.pechdavid.webweaver.structured.{ParsedHtml, ParserEventHandler}
import cz.pechdavid.mycelium.core.module.{StartModule, PostInitialize, ModuleSpec}
import cz.pechdavid.mycelium.test.usecase.ConsumingTstModule
import akka.testkit.TestActor

/**
 * Created: 2/23/13 8:28 PM
 */
@RunWith(classOf[JUnitRunner])
class ContentParsing extends FlatSpec with ShouldMatchers {

  it should "Parse basic HTML" in {
    val queue = new LinkedBlockingDeque[String]()
    queue.add("www.root.cz")

    val targetQueue = new LinkedBlockingDeque[TestActor.Message]()

    val urlQueue = (_: ModuleSpec) => Props(new SingleUrlQueue(queue))

    new WebWeaver(Map("queue" -> urlQueue, "myProjection" -> ((_: ModuleSpec) => Props(new ConsumingTstModule("myProjection", targetQueue)))),
      List(ModuleSpec("queue"), ModuleSpec("myProjection")),
      List(new DownloadHandler),
      List(new ParserEventHandler(Set("myProjection")))
    )

    Thread.sleep(15000)

    targetQueue.size() should be (3)
    targetQueue.getFirst.msg should be(PostInitialize)
    targetQueue.getFirst.msg should be(StartModule)
    targetQueue.getFirst.msg match {
      case doc: ParsedHtml =>
        doc.title should be("Root.cz - informace nejen ze světa Linuxu")
        doc.url should be("www.root.cz")

      case other: AnyRef =>
        // fail
        other should be(null)
    }

  }

}

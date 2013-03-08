package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.webweaver.crawler.{DownloadHandler, SingleUrlQueue, WebWeaver}
import akka.actor.Props
import cz.pechdavid.webweaver.structured.{ParsedHtml, ParserEventHandler, StructuredContentProjection}
import cz.pechdavid.mycelium.core.module.{StartModule, PostInitialize, ModuleSpec}
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.raw.{RawEventHandler, RawContentTrl, RawFile}
import java.util.concurrent.LinkedBlockingDeque
import akka.testkit.TestActor
import cz.pechdavid.mycelium.test.usecase.ConsumingTstModule

/**
 * Created: 2/23/13 8:28 PM
 */
@RunWith(classOf[JUnitRunner])
class RawContent extends FlatSpec with ShouldMatchers {

  it should "Store raw" in {
    val con = ConnectionParams("localhost", "mycelium")

    val ww = new WebWeaver(Map("raw" -> ((_: ModuleSpec) => Props(new StructuredContentProjection(con)))),
      List(ModuleSpec("raw")),
      List.empty,
      List.empty
    )

    Thread.sleep(1000)

    ww.node.moduleRef("raw") ! RawFile("www.root.cz", "aaabbbccc")

    Thread.sleep(1000)

    val trl = new RawContentTrl(con)

    val docOp = trl.byUrl("www.root.cz")

    docOp.isDefined should be(true)
    val doc = docOp.get
    doc.size should be > (0)
    doc.inputStream.available() should be > (0)
    doc.inputStream.close()
  }

  it should "Process raw event handler" in {
    val queue = new LinkedBlockingDeque[String]()
    queue.add("www.root.cz")

    val targetQueue = new LinkedBlockingDeque[TestActor.Message]()
    val urlQueue = (_: ModuleSpec) => Props(new SingleUrlQueue(queue))

    new WebWeaver(Map("queue" -> urlQueue, "myProjection" -> ((_: ModuleSpec) => Props(new ConsumingTstModule("myProjection", targetQueue)))),
      List(ModuleSpec("queue"), ModuleSpec("myProjection")),
      List(new DownloadHandler),
      List(new RawEventHandler(Set("myProjection")))
    )

    Thread.sleep(15000)

    targetQueue.size() should be(3)
    targetQueue.pop.msg should be(PostInitialize)
    targetQueue.pop.msg should be(StartModule)
    targetQueue.pop.msg match {
      case doc: RawFile =>
        doc.url should be("www.root.cz")

      case other: AnyRef =>
        // fail
        other should be(null)
    }
  }
}

package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.webweaver.crawler.{DownloadHandler, WebWeaver, SingleUrlQueue}
import akka.actor.Props
import cz.pechdavid.webweaver.structured.{StructuredContentTrl, StructuredContentProjection, ParsedHtml, ParserEventHandler}
import cz.pechdavid.mycelium.core.module.{StartModule, PostInitialize, ModuleSpec}
import cz.pechdavid.mycelium.test.usecase.ConsumingTstModule
import akka.testkit.TestActor
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams

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

    targetQueue.size() should be(3)
    targetQueue.pop.msg should be(PostInitialize)
    targetQueue.pop.msg should be(StartModule)
    targetQueue.pop.msg match {
      case doc: ParsedHtml =>
        doc.title should be("Root.cz - informace nejen ze světa Linuxu")
        doc.url should be("www.root.cz")

      case other: AnyRef =>
        // fail
        other should be(null)
    }

  }

  it should "Store parsed" in {
    val con = ConnectionParams("localhost", "mycelium")
    val ww = new WebWeaver(Map("structured" -> ((_: ModuleSpec) => Props(new StructuredContentProjection(con)))),
      List(ModuleSpec("structured")),
      List.empty,
      List.empty
    )

    Thread.sleep(1000)

    ww.node.moduleRef("structured") ! ParsedHtml("www.root.cz", "Ukazka textu")

    Thread.sleep(1000)

    val trl = new StructuredContentTrl(con)

    val docOp = trl.byUrl("www.root.cz")

    docOp.isDefined should be(true)
    val doc = docOp.get
    doc.get("url") should be("www.root.cz")
  }
}

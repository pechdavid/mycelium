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
import cz.pechdavid.webweaver.raw.{RawContentTrl, RawFile}

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
    doc.size should be >(0)
    ???
    doc.inputStream
  }

  it should "Process raw event handler" in {
  }
}

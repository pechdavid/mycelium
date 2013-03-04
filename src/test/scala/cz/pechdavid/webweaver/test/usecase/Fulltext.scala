package cz.pechdavid.webweaver.test.usecase

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import java.util.concurrent.LinkedBlockingDeque
import cz.pechdavid.webweaver.crawler.WebWeaver
import cz.pechdavid.mycelium.core.module.ModuleSpec
import cz.pechdavid.webweaver.fts.{FulltextResult, FulltextSearch, FulltextProjection}
import akka.actor.Props
import cz.pechdavid.webweaver.structured.ParsedHtml
import akka.testkit.TestActor
import cz.pechdavid.mycelium.test.usecase.ConsumingTstModule

/**
 * Created: 2/24/13 6:39 PM
 */
@RunWith(classOf[JUnitRunner])
class Fulltext extends FlatSpec with ShouldMatchers {

  it should "index and search by fulltext" in {

    val targetQueue = new LinkedBlockingDeque[TestActor.Message]()

    val ww = new WebWeaver(Map("fulltext" -> ((_: ModuleSpec) => Props(new FulltextProjection)),
      "output" -> ((_: ModuleSpec) => Props(new ConsumingTstModule("output", targetQueue)))),
      List(ModuleSpec("fulltext"), ModuleSpec("output"))
    )

    Thread.sleep(1000)

    ww.node.moduleRef("fulltext") ! ParsedHtml("www.url.cz", "Ukazka textu")

    Thread.sleep(2000)

    targetQueue.clear()

    ww.node.moduleRef("fulltext") ! FulltextSearch("textu ukazka", "output")

    Thread.sleep(2000)

    targetQueue.size() should be(1)
    targetQueue.getFirst.msg should be(Array(FulltextResult("www.url.cz", "Ukazka textu")))
  }
}

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
import cz.pechdavid.mycelium.extension.mongo.ConnectionParams
import cz.pechdavid.webweaver.crawler.WebWeaver
import cz.pechdavid.mycelium.core.module.ModuleSpec
import akka.actor.Props
import cz.pechdavid.webweaver.structured.{StructuredContentTrl, ParsedHtml, StructuredContentProjection}
import cz.pechdavid.webweaver.graph.{GraphTrl, GraphProjection}

/**
 * Created: 3/8/13 8:18 PM
 */
@RunWith(classOf[JUnitRunner])
class GraphEverything extends FlatSpec with ShouldMatchers {

  it should "Store into graph" in {
    val con = ConnectionParams("localhost", "mycelium")
    val ww = new WebWeaver(Map("graphProjection" -> ((_: ModuleSpec) => Props(new GraphProjection))),
      List(ModuleSpec("graphProjection")),
      List.empty,
      List.empty
    )

    Thread.sleep(1000)

    ww.node.moduleRef("graphProjection") ! ParsedHtml("www.root.cz", "Ukazka textu", Set("/registrace/", "http://www.root.cz/omega/"))

    Thread.sleep(1000)

    val trl = new GraphTrl(con)

    val docOp = trl.treeFromUrl("www.root.cz")

    docOp.name should be("www.root.cz")

    val reg = docOp.children.head
    reg.name should be ("www.root.cz/registrace/")
    reg.children.isEmpty should be (true)

    val omega = docOp.children.tail.head
    omega.name should be ("www.root.cz/omega/")
    omega.children.isEmpty should be (true)
  }
}

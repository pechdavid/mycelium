/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.usecase

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.node.SystemNode
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 9:02 PM
 */
@RunWith(classOf[JUnitRunner])
class NodeAutodiscovery extends FlatSpec with ShouldMatchers {

  it should "Discover new nodes" in {

    val systemA = new SystemNode
    val systemB = new SystemNode
    val systemC = new SystemNode

    systemA.boot(Set.empty, List.empty)
    systemB.boot(Set.empty, List.empty)
    systemC.boot(Set.empty, List.empty)

    Thread.sleep(2000)

    systemA.container.globalNodes.size should be(3)
    systemB.container.globalNodes.size should be(3)
    systemC.container.globalNodes.size should be(3)
  }

}

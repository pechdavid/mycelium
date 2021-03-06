/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.test.operator

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.core.module
import cz.pechdavid.mycelium.core.operator.DependencyLinearizer
import module.ModuleSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:14 PM
 */
@RunWith(classOf[JUnitRunner])
class DependencyLinearizerTest extends FlatSpec with ShouldMatchers {

  /*
   * A
   * B
   * C -> A, B
   * D -> A, C (-> A, B)
   * E -> B
   */

  it should "Linearize deps" in {
    val liner = new DependencyLinearizer(Set(
      ModuleSpec("A", Set.empty),
      ModuleSpec("B", Set.empty),
      ModuleSpec("C", Set("A", "B")),
      ModuleSpec("D", Set("A", "C")),
      ModuleSpec("E", Set("B")),
      ModuleSpec("F", Set("E"))
    ))

    liner.calculate(Set.empty, List("F")) should be(List("B", "E", "F"))
    liner.calculate(Set.empty, List("F", "D")) should be(List("A", "C", "D", "B", "E", "F"))
    liner.calculate(Set("E", "A"), List("F", "D")) should be(List("C", "D", "B", "F"))
  }

  it should "Throw cycle error" in {
    evaluating {
      new DependencyLinearizer(Set(
        ModuleSpec("A", Set("C")),
        ModuleSpec("B", Set("A")),
        ModuleSpec("C", Set("B"))
      ))
    } should produce[Exception]
  }

}

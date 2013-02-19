package cz.pechdavid.mycelium.test

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import cz.pechdavid.mycelium.Launcher
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/15/13 6:09 PM
 */
@RunWith(classOf[JUnitRunner])
class LauncherTest extends FlatSpec with ShouldMatchers {

  it should "Launch application with default config" in {
    Launcher.main(Array.empty)

    Thread.sleep(2000)

    ???
  }

}

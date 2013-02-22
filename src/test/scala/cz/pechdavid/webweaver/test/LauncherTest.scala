package cz.pechdavid.webweaver.test

import cz.pechdavid.webweaver.Launcher
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * Created: 2/22/13 5:57 PM
 */
@RunWith(classOf[JUnitRunner])
class LauncherTest extends FlatSpec with ShouldMatchers {
  Launcher.main(Array.empty)

  Thread.sleep(2000)

  ???
}

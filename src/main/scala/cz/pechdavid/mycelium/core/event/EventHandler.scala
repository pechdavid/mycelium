package cz.pechdavid.mycelium.core.event

/**
 * Created: 2/16/13 11:20 PM
 */
trait EventHandler {
  def handle: PartialFunction[Any, Unit]

  def notifyProjections(set: Set[String], obj: Any) {
    ???
  }

}

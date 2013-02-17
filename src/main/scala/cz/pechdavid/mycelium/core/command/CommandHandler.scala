package cz.pechdavid.mycelium.core.command

/**
 * Created: 2/16/13 11:20 PM
 */
trait CommandHandler {
  def handle: PartialFunction[AnyRef, Unit]
}

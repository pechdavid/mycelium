package cz.pechdavid.mycelium.core.module

/**
 * Created: 2/15/13 5:50 PM
 */

sealed trait ControlMessage
case class PostInitialize(args: Option[AnyRef] = None) extends ControlMessage
case object StartModule extends ControlMessage
case object StopModule extends ControlMessage
case object PostStop extends ControlMessage
case class DependencyNotOnline(name: String) extends ControlMessage
case class DependencyOnline(name: String) extends ControlMessage

case class EventSubscribe(key: String)
case class EventOccured(key: String)

case object StatsTick
case class UpdateStats(stast: Map[String, AnyVal])

case object SelfCheck
case object CheckOk



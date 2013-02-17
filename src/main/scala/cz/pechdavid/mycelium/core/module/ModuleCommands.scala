package cz.pechdavid.mycelium.core.module

/**
 * Created: 2/15/13 5:50 PM
 */

case class PostInitialize(args: Option[AnyRef] = None)
case object StartModule
case object StopModule
case object PostStop
case object RecoverModule
case class DependencyNotOnline(name: String)

case class EventSubscribe(key: String)
case class EventOccured(key: String)

case object StatsTick
case class UpdateStats(stast: Map[String, AnyVal])

case object SelfCheck
case object CheckOk



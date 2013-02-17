package cz.pechdavid.mycelium.core.module

import akka.actor.Props

/**
 * Created: 2/17/13 6:44 PM
 */
case class StartNewModule(name: String, props: Props)

/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.mycelium.core.projection

/**
 * Notify command for EventHandlers
 *
 * Created: 2/21/13 12:03 AM
 */
case class NotifyProjections(projections: Set[String], msg: AnyRef)

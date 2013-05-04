/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.fts

/**
 * Standard search
 *
 * Created: 2/24/13 7:02 PM
 */
case class FulltextSearch(query: String, targetModule: Option[String] = None)

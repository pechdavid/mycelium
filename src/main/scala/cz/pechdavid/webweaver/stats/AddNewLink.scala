/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.stats

/**
 * Command for adding new link to domain
 * Created: 4/13/13 11:28 PM
 */
case class AddNewLink(url: String, links: Set[String]) {
  require(!links.isEmpty)
}

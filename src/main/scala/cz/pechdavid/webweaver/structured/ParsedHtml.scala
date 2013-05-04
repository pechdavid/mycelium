/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.structured

/**
 * Parsed document DTO
 *
 * Created: 2/23/13 9:31 PM
 */
case class ParsedHtml(url: String, title: String, links: Set[String] = Set.empty, images: Set[String] = Set.empty)

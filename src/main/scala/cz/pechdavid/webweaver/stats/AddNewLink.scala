package cz.pechdavid.webweaver.stats

/**
 * Created: 4/13/13 11:28 PM
 */
case class AddNewLink(url: String, links: Set[String]) {
  require(!links.isEmpty)
}

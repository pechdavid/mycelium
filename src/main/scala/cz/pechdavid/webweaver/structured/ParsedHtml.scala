package cz.pechdavid.webweaver.structured

/**
 * Created: 2/23/13 9:31 PM
 */
case class ParsedHtml(url: String, title: String, links: Set[String] = Set.empty, images: Set[String] = Set.empty)

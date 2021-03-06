/**
 * Mycelium Master's Thesis
 * David Pech
 * FIT Licence
 * 2013
 */
package cz.pechdavid.webweaver.fts

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.{JNothing, JValue}
import org.apache.lucene.index._
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.util.Version
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.document.{LongField, StringField, TextField, Document}
import org.apache.lucene.document.Field.Store
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search._
import org.apache.lucene.search.NRTManager.TrackingIndexWriter
import cz.pechdavid.webweaver.structured.ParsedHtml
import scala.Some
import org.apache.lucene.search.SortField.Type

/**
 * Lucene fulltext projection
 *
 * Created: 2/24/13 5:45 PM
 */
class FulltextProjection extends WorkerModule("fulltextProjection") {
  val analyzer = new StandardAnalyzer(Version.LUCENE_41)
  val index = new RAMDirectory()
  val config = new IndexWriterConfig(Version.LUCENE_41, analyzer)
  val writer = new TrackingIndexWriter(new IndexWriter(index, config))
  val manager = new NRTManager(writer, new SearcherFactory, true)
  val parser = new QueryParser(Version.LUCENE_41, "title", analyzer)

  def extract(parsedPayload: JValue) = {
    parsedPayload \ "query" match {
      case JNothing =>
        parsedPayload.extract[ParsedHtml]
      case _ =>
        parsedPayload.extract[FulltextSearch]
    }
  }

  def prepareDoc(html: ParsedHtml) = {
    val doc = new Document
    doc.add(new TextField("title", html.title, Store.YES))
    doc.add(new StringField("url", html.url, Store.YES))
    doc.add(new LongField("insertedAt", System.currentTimeMillis(), Store.NO))
    doc.add(new StringField("type", "page", Store.NO))

    doc
  }

  private def translateResults(query: Query, limit: Int, sort: Option[Sort] = None) = {
    manager.maybeRefresh()
    val searcher = manager.acquire()

    val docs = sort match {
      case Some(s) =>
        searcher.search(query, limit, s)
      case None =>
        searcher.search(query, limit)
    }

    val res = for (dId <- docs.scoreDocs;
                   doc = searcher.doc(dId.doc)) yield {
      FulltextResult(doc.get("url"), doc.get("title"))
    }
    manager.release(searcher)

    res
  }

  def handle = {
    case html: ParsedHtml =>
      writer.addDocument(prepareDoc(html))

    case search: FulltextSearch =>
      val query = if (search.query.isEmpty) {
          new TermQuery(new Term("type", "page"))
      } else {
        parser.parse(search.query)
      }

      val res = translateResults(query, 50)

      search.targetModule match {
        case Some(x) =>
          moduleRef(x) ! res

        case None =>
          sender ! res
      }
    case FulltextRecent =>
      val query = new TermQuery(new Term("type", "page"))

      val res = translateResults(query, 10, Option(new Sort(new SortField("insertedAt", Type.LONG, true))))
      sender ! res
  }
}

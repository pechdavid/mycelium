package cz.pechdavid.webweaver.fts

import cz.pechdavid.mycelium.core.module.WorkerModule
import net.liftweb.json.JsonAST.{JNothing, JValue}
import org.apache.lucene.index._
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.util.Version
import org.apache.lucene.store.RAMDirectory
import cz.pechdavid.webweaver.structured.ParsedHtml
import org.apache.lucene.document.{StringField, TextField, Document}
import org.apache.lucene.document.Field.Store
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.{SearcherFactory, NRTManager}
import org.apache.lucene.search.NRTManager.TrackingIndexWriter

/**
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

    doc
  }

  def handle = {
    case html: ParsedHtml =>
      writer.addDocument(prepareDoc(html))

    case search: FulltextSearch =>
      val query = parser.parse(search.query)
      manager.maybeRefresh()
      val searcher = manager.acquire()
      val docs = searcher.search(query, 50)

      val res = for (dId <- docs.scoreDocs;
                     doc = searcher.doc(dId.doc)) yield {
        FulltextResult(doc.get("url"), doc.get("title"))
      }
      manager.release(searcher)

      moduleRef(search.targetModule) ! res
  }
}

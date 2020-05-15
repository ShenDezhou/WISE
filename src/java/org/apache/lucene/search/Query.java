package org.apache.lucene.search;



import java.io.IOException;
import java.util.Hashtable;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;

/** The abstract base class for queries.
  <p>Instantiable subclasses are:
  <ul>
  <li> {@link TermQuery}
  <li> {@link PhraseQuery}
  <li> {@link BooleanQuery}
  </ul>
  <p>A parser for queries is contained in:
  <ul>
  <li><a href="doc/lucene.queryParser.QueryParser.html">QueryParser</a>
  </ul>
  */
abstract public class Query {

  // query weighting
  abstract float sumOfSquaredWeights(Searcher searcher) throws IOException;
  abstract void normalize(float norm);

  // query evaluation
  abstract Scorer scorer(IndexReader reader) throws IOException;

  void prepare(IndexReader reader) {}

  static Scorer scorer(Query query, Searcher searcher, IndexReader reader)
    throws IOException {
    query.prepare(reader);
    float sum = query.sumOfSquaredWeights(searcher);
    float norm = 1.0f / (float)Math.sqrt(sum);
    query.normalize(norm);
    return query.scorer(reader);
  }

  /** Prints a query to a string, with <code>field</code> as the default field
    for terms.
    <p>The representation used is one that is readable by
    <a href="doc/lucene.queryParser.QueryParser.html">QueryParser</a>
    (although, if the query was created by the parser, the printed
    representation may not be exactly what was parsed). */
  abstract public String toString(String field);
}

package org.apache.lucene.search;



import java.io.IOException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

/** The abstract base class for search implementations.
  <p>Subclasses implement search over a single index, over multiple indices,
  and over indices on remote servers.
 */
public abstract class Searcher {

  /** Returns the documents matching <code>query</code>. */
  public final Hits search(Query query) throws IOException {
    return search(query, null);
  }

  /** Returns the documents matching <code>query</code> and
    <code>filter</code>. */
  public final Hits search(Query query, Filter filter) throws IOException {
    return new Hits(this, query, filter);
  }

  /** Frees resources associated with this Searcher. */
  abstract public void close() throws IOException;

  abstract int docFreq(Term term) throws IOException;
  abstract int maxDoc() throws IOException;
  abstract TopDocs search(Query query, Filter filter, int n)
       throws IOException;
  abstract Document doc(int i) throws IOException;

}

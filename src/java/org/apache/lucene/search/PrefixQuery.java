package org.apache.lucene.search;



import java.io.IOException;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.IndexReader;

/** A Query that matches documents containing terms with a specified prefix. */
final public class PrefixQuery extends Query {
  private Term prefix;
  private IndexReader reader;
  private float boost = 1.0f;
  private BooleanQuery query;

  /** Constructs a query for terms starting with <code>prefix</code>. */
  public PrefixQuery(Term prefix) {
    this.prefix = prefix;
    this.reader = reader;
  }

  /** Sets the boost for this term to <code>b</code>.  Documents containing
    this term will (in addition to the normal weightings) have their score
    multiplied by <code>boost</code>. */
  public void setBoost(float boost) {
    this.boost = boost;
  }

  /** Returns the boost for this term. */
  public float getBoost() {
    return boost;
  }
  
  final void prepare(IndexReader reader) {
    this.query = null;
    this.reader = reader;
  }

  final float sumOfSquaredWeights(Searcher searcher)
    throws IOException {
    return getQuery().sumOfSquaredWeights(searcher);
  }

  void normalize(float norm) {
    try {
      getQuery().normalize(norm);
    } catch (IOException e) {
      throw new RuntimeException(e.toString());
    }
  }

  Scorer scorer(IndexReader reader) throws IOException {
    return getQuery().scorer(reader);
  }

  private BooleanQuery getQuery() throws IOException {
    if (query == null) {
      BooleanQuery q = new BooleanQuery();
      TermEnum enum = reader.terms(prefix);
      try {
	String prefixText = prefix.text();
	String prefixField = prefix.field();
	do {
	  Term term = enum.term();
	  if (term != null &&
	      term.text().startsWith(prefixText) &&
	      term.field() == prefixField) {
	    TermQuery tq = new TermQuery(term);	  // found a match
	    tq.setBoost(boost);			  // set the boost
	    q.add(tq, false, false);		  // add to q
	    //System.out.println("added " + term);
	  } else {
	    break;
	  }
	} while (enum.next());
      } finally {
	enum.close();
      }
      query = q;
    }
    return query;
  }

  /** Prints a user-readable version of this query. */
  public String toString(String field) {
    StringBuffer buffer = new StringBuffer();
    if (!prefix.field().equals(field)) {
      buffer.append(prefix.field());
      buffer.append(":");
    }
    buffer.append(prefix.text());
    buffer.append('*');
    if (boost != 1.0f) {
      buffer.append("^");
      buffer.append(Float.toString(boost));
    }
    return buffer.toString();
  }
}

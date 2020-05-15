package org.apache.lucene.search;



/** A clause in a BooleanQuery. */
public final class BooleanClause {
  /** The query whose matching documents are combined by the boolean query. */
  public Query query;
  /** If true, documents documents which <i>do not</i>
    match this sub-query will <it>not</it> match the boolean query. */
  public boolean required = false;
  /** If true, documents documents which <i>do</i>
    match this sub-query will <it>not</it> match the boolean query. */
  public boolean prohibited = false;
  
  /** Constructs a BooleanClause with query <code>q</code>, required
    <code>r</code> and prohibited <code>p</code>. */ 
  public BooleanClause(Query q, boolean r, boolean p) {
    query = q;
    required = r;
    prohibited = p;
  }
}

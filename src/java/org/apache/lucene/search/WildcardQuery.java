package org.apache.lucene.search;



import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import java.io.IOException;

/** Implements the wildcard search query */
final public class WildcardQuery extends MultiTermQuery {
    private Term wildcardTerm;

    public WildcardQuery(Term term) {
        super(term);
        wildcardTerm = term;
    }

    final void prepare(IndexReader reader) {
        try {
            setEnum(new WildcardTermEnum(reader, wildcardTerm));
        } catch (IOException e) {}
    }
    
}

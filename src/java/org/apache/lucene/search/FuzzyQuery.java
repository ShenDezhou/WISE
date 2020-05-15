package org.apache.lucene.search;



import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import java.io.IOException;

/** Implements the fuzzy search query */
final public class FuzzyQuery extends MultiTermQuery {
    private Term fuzzyTerm;
    
    public FuzzyQuery(Term term) {
        super(term);
        fuzzyTerm = term;
    }
    
    final void prepare(IndexReader reader) {
        try {
            setEnum(new FuzzyTermEnum(reader, fuzzyTerm));
        } catch (IOException e) {}
    }
    
    public String toString(String field) {
        return super.toString(field) + '~';
    }
}

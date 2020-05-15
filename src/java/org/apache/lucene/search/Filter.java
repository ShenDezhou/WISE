package org.apache.lucene.search;



import java.util.BitSet;
import java.io.IOException;
import org.apache.lucene.index.IndexReader;

/** Abstract base class providing a mechanism to restrict searches to a subset
 of an index. */
abstract public class Filter {
  /** Returns a BitSet with true for documents which should be permitted in
    search results, and false for those that should not. */
  abstract public BitSet bits(IndexReader reader) throws IOException;
}

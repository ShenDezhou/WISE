package org.apache.lucene.search;



import java.io.IOException;
import org.apache.lucene.index.Term;

/** Internal class used for scoring.
 * <p>Public only so that the indexing code can compute and store the
 * normalization byte for each document. */
public final class Similarity {
  private Similarity() {}			  // no public constructor

  /** Computes the normalization byte for a document given the total number of
   * terms contained in the document.  These values are stored in an index and
   * used by the search code. */
  public static final byte norm(int numTerms) {
    // Scales 1/sqrt(numTerms) into a byte, i.e. 256/sqrt(numTerms).
    // Math.ceil is used to ensure that even very long documents don't get a
    // zero norm byte, as that is reserved for zero-lengthed documents and
    // deleted documents.
    return (byte) Math.ceil(255.0 / Math.sqrt(numTerms));
  }


  private static final float[] makeNormTable() {
    float[] result = new float[256];
    for (int i = 0; i < 256; i++)
      result[i] = i / 255.0F;
    return result;
  }

  static final float[] NORM_TABLE = makeNormTable();
    
  static final float norm(byte normByte) {
    // Un-scales from the byte encoding of a norm into a float, i.e.,
    // approximately 1/sqrt(numTerms).
    return NORM_TABLE[normByte & 0xFF];
  }

  static final float tf(int freq) {
    return (float)Math.sqrt(freq);
  }

  static final float tf(float freq) {
    return (float)Math.sqrt(freq);
  }
    
  static final float idf(Term term, Searcher searcher) throws IOException {
    // Use maxDoc() instead of numDocs() because its proportional to docFreq(),
    // i.e., when one is inaccurate, so is the other, and in the same way.
    return idf(searcher.docFreq(term), searcher.maxDoc());
  }

  static final float idf(int docFreq, int numDocs) {
    return (float)(Math.log(numDocs/(double)(docFreq+1)) + 1.0);
  }
    
  static final float coord(int overlap, int maxOverlap) {
    return overlap / (float)maxOverlap;
  }
}

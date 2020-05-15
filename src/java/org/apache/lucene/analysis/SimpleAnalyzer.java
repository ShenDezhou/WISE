package org.apache.lucene.analysis;



import java.io.Reader;

/** An Analyzer that filters LetterTokenizer with LowerCaseFilter. */

public final class SimpleAnalyzer extends Analyzer {
  public final TokenStream tokenStream(String fieldName, Reader reader) {
    return new LowerCaseTokenizer(reader);
  }
}

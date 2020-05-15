package org.apache.lucene.analysis;



import java.io.Reader;
import java.util.Hashtable;

/** Filters LetterTokenizer with LowerCaseFilter and StopFilter. */

public final class StopAnalyzer extends Analyzer {
  private Hashtable stopTable;

  /** An array containing some common English words that are not usually useful
    for searching. */
  public static final String[] ENGLISH_STOP_WORDS = {
    "a", "and", "are", "as", "at", "be", "but", "by",
    "for", "if", "in", "into", "is", "it",
    "no", "not", "of", "on", "or", "s", "such",
    "t", "that", "the", "their", "then", "there", "these",
    "they", "this", "to", "was", "will", "with"
  };

  /** Builds an analyzer which removes words in ENGLISH_STOP_WORDS. */
  public StopAnalyzer() {
    stopTable = StopFilter.makeStopTable(ENGLISH_STOP_WORDS);
  }

  /** Builds an analyzer which removes words in the provided array. */
  public StopAnalyzer(String[] stopWords) {
    stopTable = StopFilter.makeStopTable(stopWords);
  }

  /** Filters LowerCaseTokenizer with StopFilter. */
  public final TokenStream tokenStream(String fieldName, Reader reader) {
    return new StopFilter(new LowerCaseTokenizer(reader), stopTable);
  }
}


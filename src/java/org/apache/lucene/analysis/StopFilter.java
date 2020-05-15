package org.apache.lucene.analysis;



import org.apache.lucene.analysis.standard.ParseException;

import java.io.IOException;
import java.util.Hashtable;

/** Removes stop words from a token stream. */

public final class StopFilter extends TokenFilter {

  private Hashtable table;

  /** Constructs a filter which removes words from the input
    TokenStream that are named in the array of words. */
  public StopFilter(TokenStream in, String[] stopWords) {
    input = in;
    table = makeStopTable(stopWords);
  }

  /** Constructs a filter which removes words from the input
    TokenStream that are named in the Hashtable. */
  public StopFilter(TokenStream in, Hashtable stopTable) {
    input = in;
    table = stopTable;
  }
  
  /** Builds a Hashtable from an array of stop words, appropriate for passing
    into the StopFilter constructor.  This permits this table construction to
    be cached once when an Analyzer is constructed. */
  public final static Hashtable makeStopTable(String[] stopWords) {
    Hashtable stopTable = new Hashtable(stopWords.length);
    for (int i = 0; i < stopWords.length; i++)
      stopTable.put(stopWords[i], stopWords[i]);
    return stopTable;
  }

  /** Returns the next input Token whose termText() is not a stop word. */
  public final Token next() throws IOException, ParseException {
    // return the first non-stop word found
    for (Token token = input.next(); token != null; token = input.next())
      if (table.get(token.termText) == null)
	return token;
    // reached EOS -- return null
    return null;
  }
}

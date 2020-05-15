package org.apache.lucene.analysis;



import org.apache.lucene.analysis.standard.ParseException;

/** Normalizes token text to lower case. */

public final class LowerCaseFilter extends TokenFilter {
  public LowerCaseFilter(TokenStream in) {
    input = in;
  }

  public final Token next() throws java.io.IOException, ParseException {
    Token t = input.next();

    if (t == null)
      return null;

    t.termText = t.termText.toLowerCase();

    return t;
  }
}

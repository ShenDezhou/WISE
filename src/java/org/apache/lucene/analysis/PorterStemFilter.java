package org.apache.lucene.analysis;



import org.apache.lucene.analysis.standard.ParseException;

import java.io.IOException;
import java.util.Hashtable;

/** Transforms the token stream as per the Porter stemming algorithm. 
    Note: the input to the stemming filter must already be in lower case, 
    so you will need to use LowerCaseFilter or LowerCaseTokenizer farther
    down the Tokenizer chain in order for this to work properly!  

    To use this filter with other analyzers, you'll want to write an 
    Analyzer class that sets up the TokenStream chain as you want it.  
    To use this with LowerCaseTokenizer, for example, you'd write an
    analyzer like this:

    class MyAnalyzer extends Analyzer {
      public final TokenStream tokenStream(String fieldName, Reader reader) {
        return new PorterStemFilter(new LowerCaseTokenizer(reader));
      }
    } 

*/

public final class PorterStemFilter extends TokenFilter {
  private PorterStemmer stemmer;

  public PorterStemFilter(TokenStream in) {
    stemmer = new PorterStemmer();
    input = in;
  }

  /** Returns the next input Token, after being stemmed */
  public final Token next() throws IOException, ParseException {
    Token token = input.next();
    if (token == null)
      return null;
    else {
      String s = stemmer.stem(token.termText);
      if (s != token.termText) // Yes, I mean object reference comparison here
        token.termText = s;
      return token;
    }
  }
}

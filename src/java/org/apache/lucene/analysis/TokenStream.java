package org.apache.lucene.analysis;



import org.apache.lucene.analysis.standard.ParseException;

import java.io.IOException;

/** A TokenStream enumerates the sequence of tokens, either from
  fields of a document or from query text.
  <p>
  This is an abstract class.  Concrete subclasses are:
  <ul>
  <li>{@link Tokenizer}, a TokenStream
  whose input is a Reader; and
  <li>{@link TokenFilter}, a TokenStream
  whose input is another TokenStream.
  </ul>
  */

abstract public class TokenStream {
  /** Returns the next token in the stream, or null at EOS. */
  abstract public Token next() throws IOException, ParseException;

  /** Releases resources associated with this stream. */
  public void close() throws IOException {}
}

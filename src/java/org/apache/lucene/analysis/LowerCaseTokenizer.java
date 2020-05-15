package org.apache.lucene.analysis;



import java.io.Reader;

/** LowerCaseTokenizer performs the function of LetterTokenizer
  and LowerCaseFilter together.  It divides text at non-letters and converts
  them to lower case.  While it is functionally equivalent to the combination
  of LetterTokenizer and LowerCaseFilter, there is a performance advantage
  to doing the two tasks at once, hence this (redundent) implementation.

  Note: this does a decent job for most European languages, but does a terrible
  job for some Asian languages, where words are not separated by spaces. */

public final class LowerCaseTokenizer extends Tokenizer {
  public LowerCaseTokenizer(Reader in) {
    input = in;
  }

  private int offset = 0, bufferIndex=0, dataLen=0;
  private final static int MAX_WORD_LEN = 255;
  private final static int IO_BUFFER_SIZE = 1024;
  private final char[] buffer = new char[MAX_WORD_LEN];
  private final char[] ioBuffer = new char[IO_BUFFER_SIZE];

  public final Token next() throws java.io.IOException {
    int length = 0;
    int start = offset;
    while (true) {
      final char c;

      offset++;
      if (bufferIndex >= dataLen) {
        dataLen = input.read(ioBuffer);
        bufferIndex = 0;
      };
      if (dataLen == -1) {
	if (length > 0)
	  break;
	else
	  return null;
      }
      else
        c = (char) ioBuffer[bufferIndex++];
      
      if (Character.isLetter(c)) {		  // if it's a letter

	if (length == 0)			  // start of token
	  start = offset-1;

	buffer[length++] = Character.toLowerCase(c);
                                                  // buffer it
	if (length == MAX_WORD_LEN)		  // buffer overflow!
	  break;

      } else if (length > 0)			  // at non-Letter w/ chars
	break;					  // return 'em

    }

    return new Token(new String(buffer, 0, length), start, start+length);
  }
}

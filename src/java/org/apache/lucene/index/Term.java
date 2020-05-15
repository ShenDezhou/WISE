package org.apache.lucene.index;



/**
  A Term represents a word from text.  This is the unit of search.  It is
  composed of two elements, the text of the word, as a string, and the name of
  the field that the text occured in, an interned string.

  Note that terms may represent more than words from text fields, but also
  things like dates, email addresses, urls, etc.  */

public final class Term {
  String field;
  String text;
  
  /** Constructs a Term with the given field and text. */
  public Term(String fld, String txt) {
    this(fld, txt, true);
  }
  Term(String fld, String txt, boolean intern) {
    field = intern ? fld.intern() : fld;	  // field names are interned
    text = txt;					  // unless already known to be
  }

  /** Returns the field of this term, an interned string.   The field indicates
    the part of a document which this term came from. */
  public final String field() { return field; }

  /** Returns the text of this term.  In the case of words, this is simply the
    text of the word.  In the case of dates and other types, this is an
    encoding of the object as a string.  */
  public final String text() { return text; }

  /** Compares two terms, returning true iff they have the same
      field and text. */
  public final boolean equals(Object o) {
    if (o == null)
      return false;
    Term other = (Term)o;
    return field == other.field && text.equals(other.text);
  }

  /** Combines the hashCode() of the field and the text. */
  public final int hashCode() {
    return field.hashCode() + text.hashCode();
  }

  /** Compares two terms, returning an integer which is less than zero iff this
    term belongs after the argument, equal zero iff this term is equal to the
    argument, and greater than zero iff this term belongs after the argument.

    The ordering of terms is first by field, then by text.*/
  public final int compareTo(Term other) {
    if (field == other.field)			  // fields are interned
      return text.compareTo(other.text);
    else
      return field.compareTo(other.field);
  }

  /** Resets the field and text of a Term. */
  final void set(String fld, String txt) {
    field = fld;
    text = txt;
  }

  public final String toString() {
    return "Term<" + field + ":" + text + ">";
  }
}

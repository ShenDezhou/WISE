package org.apache.lucene.document;



import java.util.Enumeration;

/** Documents are the unit of indexing and search.
 *
 * A Document is a set of fields.  Each field has a name and a textual value.
 * A field may be stored with the document, in which case it is returned with
 * search hits on the document.  Thus each document should typically contain
 * stored fields which uniquely identify it.
 * */

public final class Document {
  DocumentFieldList fieldList = null;

  /** Constructs a new document with no fields. */
  public Document() {}

  /** Adds a field to a document.  Several fields may be added with
   * the same name.  In this case, if the fields are indexed, their text is
   * treated as though appended for the purposes of search. */
  public final void add(Field field) {
    fieldList = new DocumentFieldList(field, fieldList);
  }

  /** Returns a field with the given name if any exist in this document, or
    null.  If multiple fields may exist with this name, this method returns the
    last added such added. */
  public final Field getField(String name) {
    for (DocumentFieldList list = fieldList; list != null; list = list.next)
      if (list.field.name().equals(name))
	return list.field;
    return null;
  }

  /** Returns the string value of the field with the given name if any exist in
    this document, or null.  If multiple fields may exist with this name, this
    method returns the last added such added. */
  public final String get(String name) {
    Field field = getField(name);
    if (field != null)
      return field.stringValue();
    else
      return null;
  }

  /** Returns an Enumeration of all the fields in a document. */
  public final Enumeration fields() {
    return new DocumentFieldEnumeration(this);
  }

  /** Prints the fields of a document for human consumption. */
  public final String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Document<");
    for (DocumentFieldList list = fieldList; list != null; list = list.next) {
      buffer.append(list.field.toString());
      if (list.next != null)
	buffer.append(" ");
    }
    buffer.append(">");
    return buffer.toString();
  }

}

final class DocumentFieldList {
  DocumentFieldList(Field f, DocumentFieldList n) {
    field = f;
    next = n;
  }
  Field field;
  DocumentFieldList next;
}

final class DocumentFieldEnumeration implements Enumeration {
  DocumentFieldList fields;
  DocumentFieldEnumeration(Document d) {
    fields = d.fieldList;
  }

  public final boolean hasMoreElements() {
    return fields == null ? false : true;
  }

  public final Object nextElement() {
    Field result = fields.field;
    fields = fields.next;
    return result;
  }
}

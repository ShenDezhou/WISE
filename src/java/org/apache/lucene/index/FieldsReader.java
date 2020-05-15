package org.apache.lucene.index;



import java.util.Enumeration;
import java.util.Hashtable;
import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.InputStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

final class FieldsReader {
  private FieldInfos fieldInfos;
  private InputStream fieldsStream;
  private InputStream indexStream;
  private int size;

  FieldsReader(Directory d, String segment, FieldInfos fn)
       throws IOException {
    fieldInfos = fn;

    fieldsStream = d.openFile(segment + ".fdt");
    indexStream = d.openFile(segment + ".fdx");

    size = (int)indexStream.length() / 8;
  }

  final void close() throws IOException {
    fieldsStream.close();
    indexStream.close();
  }

  final int size() {
    return size;
  }

  final Document doc(int n) throws IOException {
    indexStream.seek(n * 8L);
    long position = indexStream.readLong();
    fieldsStream.seek(position);
    
    Document doc = new Document();
    int numFields = fieldsStream.readVInt();
    for (int i = 0; i < numFields; i++) {
      int fieldNumber = fieldsStream.readVInt();
      FieldInfo fi = fieldInfos.fieldInfo(fieldNumber);

      byte bits = fieldsStream.readByte();

      doc.add(new Field(fi.name,		  // name
			fieldsStream.readString(), // read value
			true,			  // stored
			fi.isIndexed,		  // indexed
			(bits & 1) != 0));	  // tokenized
    }

    return doc;
  }
}

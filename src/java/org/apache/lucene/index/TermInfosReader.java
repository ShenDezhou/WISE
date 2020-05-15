package org.apache.lucene.index;



import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.InputStream;

/** This stores a monotonically increasing set of <Term, TermInfo> pairs in a
 * Directory.  Pairs are accessed either by Term or by ordinal position the
 * set.  */

final class TermInfosReader {
  private Directory directory;
  private String segment;
  private FieldInfos fieldInfos;

  private SegmentTermEnum enum;
  private int size;

  TermInfosReader(Directory dir, String seg, FieldInfos fis)
       throws IOException {
    directory = dir;
    segment = seg;
    fieldInfos = fis;

    enum = new SegmentTermEnum(directory.openFile(segment + ".tis"),
			       fieldInfos, false);
    size = enum.size;
    readIndex();
  }

  final void close() throws IOException {
    if (enum != null)
      enum.close();
  }

  /** Returns the number of term/value pairs in the set. */
  final int size() {
    return size;
  }

  Term[] indexTerms = null;
  TermInfo[] indexInfos;
  long[] indexPointers;

  private final void readIndex() throws IOException {
    SegmentTermEnum indexEnum =
      new SegmentTermEnum(directory.openFile(segment + ".tii"),
			  fieldInfos, true);
    try {
      int indexSize = indexEnum.size;

      indexTerms = new Term[indexSize];
      indexInfos = new TermInfo[indexSize];
      indexPointers = new long[indexSize];

      for (int i = 0; indexEnum.next(); i++) {
	indexTerms[i] = indexEnum.term();
	indexInfos[i] = indexEnum.termInfo();
	indexPointers[i] = indexEnum.indexPointer;
      }
    } finally {
      indexEnum.close();
    }
  }

  /** Returns the offset of the greatest index entry which is less than term.*/
  private final int getIndexOffset(Term term) throws IOException {
    int lo = 0;					  // binary search indexTerms[]
    int hi = indexTerms.length - 1;

    while (hi >= lo) {
      int mid = (lo + hi) >> 1;
      int delta = term.compareTo(indexTerms[mid]);
      if (delta < 0)
	hi = mid - 1;
      else if (delta > 0)
	lo = mid + 1;
      else
	return mid;
    }
    return hi;
  }

  private final void seekEnum(int indexOffset) throws IOException {
    enum.seek(indexPointers[indexOffset],
	      (indexOffset * TermInfosWriter.INDEX_INTERVAL) - 1,
	      indexTerms[indexOffset], indexInfos[indexOffset]);
  }

  /** Returns the TermInfo for a Term in the set, or null. */
  final synchronized TermInfo get(Term term) throws IOException {
    if (size == 0) return null;
    
    // optimize sequential access: first try scanning cached enum w/o seeking
    if (enum.term() != null			  // term is at or past current
	&& ((enum.prev != null && term.compareTo(enum.prev) > 0)
	    || term.compareTo(enum.term()) >= 0)) { 
      int enumOffset = (enum.position/TermInfosWriter.INDEX_INTERVAL)+1;
      if (indexTerms.length == enumOffset	  // but before end of block
	  || term.compareTo(indexTerms[enumOffset]) < 0)
	return scanEnum(term);			  // no need to seek
    }
    
    // random-access: must seek
    seekEnum(getIndexOffset(term));
    return scanEnum(term);
  }
  
  /** Scans within block for matching term. */
  private final TermInfo scanEnum(Term term) throws IOException {
    while (term.compareTo(enum.term()) > 0 && enum.next()) {}
    if (enum.term() != null && term.compareTo(enum.term()) == 0)
      return enum.termInfo();
    else
      return null;
  }

  /** Returns the nth term in the set. */
  final synchronized Term get(int position) throws IOException {
    if (size == 0) return null;

    if (enum != null && enum.term() != null && position >= enum.position &&
	position < (enum.position + TermInfosWriter.INDEX_INTERVAL))
      return scanEnum(position);		  // can avoid seek

    seekEnum(position / TermInfosWriter.INDEX_INTERVAL); // must seek
    return scanEnum(position);
  }

  private final Term scanEnum(int position) throws IOException {
    while(enum.position < position)
      if (!enum.next())
	return null;

    return enum.term();
  }

  /** Returns the position of a Term in the set or -1. */
  final synchronized int getPosition(Term term) throws IOException {
    if (size == 0) return -1;

    int indexOffset = getIndexOffset(term);
    seekEnum(indexOffset);

    while(term.compareTo(enum.term()) > 0 && enum.next()) {}

    if (term.compareTo(enum.term()) == 0)
      return enum.position;
    else
      return -1;
  }

  /** Returns an enumeration of all the Terms and TermInfos in the set. */
  final synchronized SegmentTermEnum terms() throws IOException {
    if (enum.position != -1)			  // if not at start
      seekEnum(0);				  // reset to start
    return (SegmentTermEnum)enum.clone();
  }

  /** Returns an enumeration of terms starting at or after the named term. */
  final synchronized SegmentTermEnum terms(Term term) throws IOException {
    get(term);					  // seek enum to term
    return (SegmentTermEnum)enum.clone();
  }


}

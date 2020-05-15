package org.apache.lucene.index;



import java.io.IOException;
import org.apache.lucene.util.BitVector;

final class SegmentMergeInfo {
  Term term;
  int base;
  SegmentTermEnum termEnum;
  SegmentReader reader;
  SegmentTermPositions postings;
  int[] docMap = null;				  // maps around deleted docs

  SegmentMergeInfo(int b, SegmentTermEnum te, SegmentReader r)
    throws IOException {
    base = b;
    reader = r;
    termEnum = te;
    term = te.term();
    postings = new SegmentTermPositions(r);

    if (reader.deletedDocs != null) {
      // build array which maps document numbers around deletions 
      BitVector deletedDocs = reader.deletedDocs;
      int maxDoc = reader.maxDoc();
      docMap = new int[maxDoc];
      int j = 0;
      for (int i = 0; i < maxDoc; i++) {
	if (deletedDocs.get(i))
	  docMap[i] = -1;
	else
	  docMap[i] = j++;
      }
    }
  }

  final boolean next() throws IOException {
    if (termEnum.next()) {
      term = termEnum.term();
      return true;
    } else {
      term = null;
      return false;
    }
  }

  final void close() throws IOException {
    termEnum.close();
    postings.close();
  }
}


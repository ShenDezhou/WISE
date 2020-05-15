package org.apache.lucene.index;



import java.io.IOException;
import org.apache.lucene.util.BitVector;
import org.apache.lucene.store.InputStream;

class SegmentTermDocs implements TermDocs {
  protected SegmentReader parent;
  private InputStream freqStream;
  private int freqCount;
  private BitVector deletedDocs;
  int doc = 0;
  int freq;

  SegmentTermDocs(SegmentReader p) throws IOException {
    parent = p;
    freqStream = parent.getFreqStream();
    deletedDocs = parent.deletedDocs;
  }

  SegmentTermDocs(SegmentReader p, TermInfo ti) throws IOException {
    this(p);
    seek(ti);
  }
  
  void seek(TermInfo ti) throws IOException {
    freqCount = ti.docFreq;
    doc = 0;
    freqStream.seek(ti.freqPointer);
  }
  
  public void close() throws IOException {
    freqStream.close();
  }

  public final int doc() { return doc; }
  public final int freq() { return freq; }

  protected void skippingDoc() throws IOException {
  }

  public boolean next() throws IOException {
    while (true) {
      if (freqCount == 0)
	return false;

      int docCode = freqStream.readVInt();
      doc += docCode >>> 1;			  // shift off low bit
      if ((docCode & 1) != 0)			  // if low bit is set
	freq = 1;				  // freq is one
      else
	freq = freqStream.readVInt();		  // else read freq
 
      freqCount--;
    
      if (deletedDocs == null || !deletedDocs.get(doc))
	break;
      skippingDoc();
    }
    return true;
  }

  /** Optimized implementation. */
  public int read(final int[] docs, final int[] freqs)
      throws IOException {
    final int end = docs.length;
    int i = 0;
    while (i < end && freqCount > 0) {

      // manually inlined call to next() for speed
      final int docCode = freqStream.readVInt();
      doc += docCode >>> 1;			  // shift off low bit
      if ((docCode & 1) != 0)			  // if low bit is set
	freq = 1;				  // freq is one
      else
	freq = freqStream.readVInt();		  // else read freq
      freqCount--;
   
      if (deletedDocs == null || !deletedDocs.get(doc)) {
	docs[i] = doc;
	freqs[i] = freq;
	++i;
      }
     }
    return i;
  }

  /** As yet unoptimized implementation. */
  public boolean skipTo(int target) throws IOException {
    do {
      if (!next())
	return false;
    } while (target > doc);
    return true;
  }
}

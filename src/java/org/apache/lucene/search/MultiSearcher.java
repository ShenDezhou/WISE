package org.apache.lucene.search;



import java.io.IOException;
import java.util.Vector;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.util.PriorityQueue;

/** Implements search over a set of Searcher's. */
public final class MultiSearcher extends Searcher {
  private Searcher[] searchers;
  private int[] starts;
  private int maxDoc = 0;

  /** Creates a searcher which searches <i>searchers</i>. */
  public MultiSearcher(Searcher[] searchers) throws IOException {
    this.searchers = searchers;

    starts = new int[searchers.length + 1];	  // build starts array
    for (int i = 0; i < searchers.length; i++) {
      starts[i] = maxDoc;
      maxDoc += searchers[i].maxDoc();		  // compute maxDocs
    }
    starts[searchers.length] = maxDoc;
  }
    
  /** Frees resources associated with this Searcher. */
  public final void close() throws IOException {
    for (int i = 0; i < searchers.length; i++)
      searchers[i].close();
  }

  final int docFreq(Term term) throws IOException {
    int docFreq = 0;
    for (int i = 0; i < searchers.length; i++)
      docFreq += searchers[i].docFreq(term);
    return docFreq;
  }

  final Document doc(int n) throws IOException {
    int i = searcherIndex(n);			  // find searcher index
    return searchers[i].doc(n - starts[i]);	  // dispatch to searcher
  }

  // replace w/ call to Arrays.binarySearch in Java 1.2
  private final int searcherIndex(int n) {	  // find searcher for doc n:
    int lo = 0;					  // search starts array
    int hi = searchers.length - 1;		  // for first element less
						  // than n, return its index
    while (hi >= lo) {
      int mid = (lo + hi) >> 1;
      int midValue = starts[mid];
      if (n < midValue)
	hi = mid - 1;
      else if (n > midValue)
	lo = mid + 1;
      else
	return mid;
    }
    return hi;
  }

  final int maxDoc() throws IOException {
    return maxDoc;
  }

  final TopDocs search(Query query, Filter filter, int nDocs)
       throws IOException {
    HitQueue hq = new HitQueue(nDocs);
    float minScore = 0.0f;
    int totalHits = 0;

    for (int i = 0; i < searchers.length; i++) {  // search each searcher
      TopDocs docs = searchers[i].search(query, filter, nDocs);
      totalHits += docs.totalHits;		  // update totalHits
      ScoreDoc[] scoreDocs = docs.scoreDocs;
      for (int j = 0; j < scoreDocs.length; j++) { // merge scoreDocs into hq
	ScoreDoc scoreDoc = scoreDocs[j];
	if (scoreDoc.score >= minScore) {
	  scoreDoc.doc += starts[i];		  // convert doc
	  hq.put(scoreDoc);			  // update hit queue
	  if (hq.size() > nDocs) {		  // if hit queue overfull
	    hq.pop();				  // remove lowest in hit queue
	    minScore = ((ScoreDoc)hq.top()).score; // reset minScore
	  }
	} else
	  break;				  // no more scores > minScore
      }
    }
    
    ScoreDoc[] scoreDocs = new ScoreDoc[hq.size()];
    for (int i = hq.size()-1; i >= 0; i--)	  // put docs in array
      scoreDocs[i] = (ScoreDoc)hq.pop();
    
    return new TopDocs(totalHits, scoreDocs);
  }
}

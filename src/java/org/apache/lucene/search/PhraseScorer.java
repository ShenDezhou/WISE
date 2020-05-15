package org.apache.lucene.search;



import java.io.IOException;
import java.util.Vector;
import org.apache.lucene.util.*;
import org.apache.lucene.index.*;

abstract class PhraseScorer extends Scorer {
  protected byte[] norms;
  protected float weight;

  protected PhraseQueue pq;
  protected PhrasePositions first, last;

  PhraseScorer(TermPositions[] tps, byte[] n, float w) throws IOException {
    norms = n;
    weight = w;

    // use PQ to build a sorted list of PhrasePositions
    pq = new PhraseQueue(tps.length);
    for (int i = 0; i < tps.length; i++)
      pq.put(new PhrasePositions(tps[i], i));
    pqToList();
  }

  final void score(HitCollector results, int end) throws IOException {
    while (last.doc < end) {			  // find doc w/ all the terms
      while (first.doc < last.doc) {		  // scan forward in first
	do {
	  first.next();
	} while (first.doc < last.doc);
	firstToLast();
	if (last.doc >= end)
	  return;
      }

      // found doc with all terms
      float freq = phraseFreq();		  // check for phrase

      if (freq > 0.0) {
	float score = Similarity.tf(freq)*weight; // compute score
	score *= Similarity.norm(norms[first.doc]); // normalize
	results.collect(first.doc, score);	  // add to results
      }
      last.next();				  // resume scanning
    }
  }

  abstract protected float phraseFreq() throws IOException;

  protected final void pqToList() {
    last = first = null;
    while (pq.top() != null) {
      PhrasePositions pp = (PhrasePositions)pq.pop();
      if (last != null) {			  // add next to end of list
	last.next = pp;
      } else
	first = pp;
      last = pp;
      pp.next = null;
    }
  }

  protected final void firstToLast() {
    last.next = first;			  // move first to end of list
    last = first;
    first = first.next;
    last.next = null;
  }
}

package org.apache.lucene.search;



final class TopDocs {
  int totalHits;
  ScoreDoc[] scoreDocs;

  TopDocs(int th, ScoreDoc[] sds) {
    totalHits = th;
    scoreDocs = sds;
  }
}

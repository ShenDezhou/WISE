package org.apache.lucene.search;



final class ScoreDoc {
  float score;
  int doc;

  ScoreDoc(int d, float s) {
    doc = d;
    score = s;
  }
}

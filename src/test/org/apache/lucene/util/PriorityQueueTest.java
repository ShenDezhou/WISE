package org.apache.lucene.util;



import java.util.Date;
import java.util.Random;

class PriorityQueueTest {
  public static void main(String[] args) {
    test(10000);
  }

  public static void test(int count) {
    PriorityQueue pq = new PriorityQueue() {
      protected boolean lessThan(Object a, Object b) {
        return Integer.valueOf(a.toString()).compareTo(Integer.valueOf(b.toString())) == -1;
      }
    };
    pq.initialize(count);

    Random gen = new Random();
    int i;
    
    Date start = new Date();

    for (i = 0; i < count; i++) {
      pq.put(new Integer(gen.nextInt()));
    }

    Date end = new Date();

    System.out.print(((float)(end.getTime()-start.getTime()) / count) * 1000);
    System.out.println(" microseconds/put");

    start = new Date();

    int last = Integer.MIN_VALUE;
    for (i = 0; i < count; i++) {
      Integer next = (Integer)pq.pop();
      if (next.intValue() <= last)
	throw new Error("out of order");
      last = next.intValue();
    }

    end = new Date();

    System.out.print(((float)(end.getTime()-start.getTime()) / count) * 1000);
    System.out.println(" microseconds/pop");

  }
}

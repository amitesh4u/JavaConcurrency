package com.amitesh.concurrency;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * It implements the BlockingQueue interface
 * <p>
 * - unbounded concurrent queue - it uses the same ordering rules as the java.util.PriorityQueue
 * class -> have to implement the Comparable interface The comparable interface will determine what
 * will the order in the queue
 * <p>
 * The priority can be the same compare() == 0 case
 * <p>
 * - no null items !!!
 */

public class PriorityBlockingQueues {

  public static void main(String[] args) {
    Comparator<String> reverseStringComparator = Comparator.reverseOrder();

    /* Empty arguments will sort by natural Order */
    BlockingQueue<String> queue = new PriorityBlockingQueue<>(5, reverseStringComparator);

    ProducerWorker producerWorker = new ProducerWorker(queue);
    ConsumerWorker consumerWorker = new ConsumerWorker(queue);

    new Thread(producerWorker).start();
    new Thread(consumerWorker).start();
  }

  static class ProducerWorker implements Runnable {

    private final BlockingQueue<String> blockingQueue;

    public ProducerWorker(BlockingQueue<String> blockingQueue) {
      this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
      try {
        blockingQueue.put("B");
        blockingQueue.put("A");
        blockingQueue.put("F");
        Thread.sleep(2000);
        blockingQueue.put("H");
        Thread.sleep(1000);
        blockingQueue.put("E");
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }

  static class ConsumerWorker implements Runnable {

    private final BlockingQueue<String> blockingQueue;

    public ConsumerWorker(BlockingQueue<String> blockingQueue) {
      this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(1000);
        System.out.println(blockingQueue.take());
        Thread.sleep(2000);
        System.out.println(blockingQueue.take());
        Thread.sleep(1000);
        System.out.println(blockingQueue.take());
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }
}






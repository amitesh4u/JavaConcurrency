package com.amitesh.concurrency;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * BlockingQueue -> an interface that represents a queue that is thread safe when we Put items or
 * take items from it. We can use it with producer-consumer pattern. unbounded queue – can grow
 * almost indefinitely bounded queue – with maximal capacity defined
 * <p>
 * ## Adding Elements # add() – returns true if insertion was successful, otherwise throws an
 * IllegalStateException # put() – inserts the specified element into a queue, waiting for a free
 * slot if necessary # offer() – returns true if insertion was successful, otherwise false # offer(E
 * e, long timeout, TimeUnit unit) – tries to insert element into a queue and waits for an available
 * slot within a specified timeout
 * <p>
 * ##  Retrieving Elements # take() – waits for a head element of a queue and removes it. If the
 * queue is empty, it blocks and waits for an element to become available # poll(long timeout,
 * TimeUnit unit) – retrieves and removes the head of the queue, waiting up to the specified wait
 * time if necessary for an element to become available. Returns null after a timeout
 */

public class BlockingQueues {

  public static void main(String[] args) throws InterruptedException {

//    BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(20); // Bounded Queue
    BlockingQueue<Integer> queue = new LinkedBlockingQueue<>();  // UnBounded Queue
    ProducerWorker producerWorker = new ProducerWorker(queue);

    ConsumerWorker consumerWorker1 = new ConsumerWorker("Consumer 1", queue);
    ConsumerWorker consumerWorker2 = new ConsumerWorker("Consumer 2", queue);

    new Thread(producerWorker).start();
    new Thread(consumerWorker1).start();
    new Thread(consumerWorker2).start();

    TimeUnit.SECONDS.sleep(15);
    System.exit(0);
  }
}

class ProducerWorker implements Runnable {

  private final BlockingQueue<Integer> blockingQueue;

  public ProducerWorker(final BlockingQueue<Integer> blockingQueue) {
    this.blockingQueue = blockingQueue;
  }

  @Override
  public void run() {
    try {
      for (int i = 1; i <= 100; i++) {
        Thread.sleep(50);
        System.out.println(STR."Adding \{i}");
        /* PUT - inserts the specified element into a queue, waiting for a free slot if necessary

         */
        blockingQueue.put(i);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }
  }
}

class ConsumerWorker implements Runnable {

  private final String id;
  private final BlockingQueue<Integer> blockingQueue;

  public ConsumerWorker(String id, final BlockingQueue<Integer> blockingQueue) {
    this.id = id;
    this.blockingQueue = blockingQueue;
  }

  @Override
  public void run() {
    try {
      while (true) {
        System.out.println(STR."\{id} is waiting to consume...");
        Thread.sleep(200);
        System.out.println(STR."Current Queue size is \{blockingQueue.size()}");
        System.out.println(STR."\{id} is consuming \{blockingQueue.take()}");
      }
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }
  }
}

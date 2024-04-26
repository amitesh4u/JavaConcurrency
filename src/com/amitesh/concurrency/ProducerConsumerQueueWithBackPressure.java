package com.amitesh.concurrency;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * With back pressure i.e. Queue capacity, queue will stop accepting new data till consumer consumes some
 */

public class ProducerConsumerQueueWithBackPressure {

  private static int TOTAL_DATA = 1000;
  private static int PROCESSED_DATA = 0;

  public static void main(String[] args) throws IOException {
    ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();

    DummyProducer producer = new DummyProducer(threadSafeQueue);
    DummyConsumer consumer = new DummyConsumer(threadSafeQueue);

    producer.start();
    consumer.start();
  }

  private static class DummyConsumer extends Thread {
    private final ThreadSafeQueue queue;

    public DummyConsumer(ThreadSafeQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      while (true) {
        Object data = queue.remove();
        if (data == null) {
          System.out.println("No more data to process from the queue, consumer is terminating");
          break;
        }

        process(data);
      }
    }

    /* Any processing task that should take more time  than producer */
    private void process(Object data) {
      try {
        Thread.sleep(10);
        PROCESSED_DATA++;
        System.out.println(STR."Processed Data \{PROCESSED_DATA}");
      } catch (InterruptedException e) {
        // Ignore
      }
    }
  }

  private static class DummyProducer extends Thread {

    private static final Object DUMMY_OBJECT = new Object();
    private final ThreadSafeQueue queue;

    public DummyProducer(ThreadSafeQueue queue) {
      this.queue = queue;
    }

    @Override
    public void run() {
      while (true) {
        Object data = getData();
        if (data == null) {
          queue.terminate();
          System.out.println("No more data to process. Producer Thread is terminating");
          return;
        }

        queue.add(data);
      }
    }

    /* Any producer task that should take less time than consumer */
    private Object getData() {
      try {
        Thread.sleep(2);
      } catch (InterruptedException e) {
        // Ignore
      }
      if(TOTAL_DATA > 0){
        TOTAL_DATA--;
        System.out.println(STR."Total Data \{TOTAL_DATA}");
        return DUMMY_OBJECT;
      }
      return null;
    }
  }

  private static class ThreadSafeQueue {
    private final Queue<Object> queue = new LinkedList<>();
    private boolean isEmpty = true;
    private boolean isTerminate = false;
    private static final int CAPACITY = 20;

    public synchronized void add(Object data) {
      while (queue.size() == CAPACITY) {
        try {
          wait();
        } catch (InterruptedException e) {
          // Ignore
        }
      }
      queue.add(data);
      isEmpty = false;
      notify();
    }

    public synchronized Object remove() {
      while (isEmpty && !isTerminate) {
        try {
          wait();
        } catch (InterruptedException e) {
          // Ignore
        }
      }

      if (queue.size() == 1) {
        isEmpty = true;
      }

      if (queue.isEmpty() && isTerminate) {
        return null;
      }

      System.out.println(STR."Queue size \{queue.size()}");

      Object result = queue.remove();
      if (queue.size() == CAPACITY - 1) {
        notifyAll();
      }
      return result;
    }

    public synchronized void terminate() {
      isTerminate = true;
      //notifyAll();
    }
  }
}

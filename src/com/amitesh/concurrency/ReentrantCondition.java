package com.amitesh.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * While Lock offers an alternative to the synchronized methods,
 * Condition offers an alternative to the Object monitor methods like wait, notify, and notifyAll.
 */

public class ReentrantCondition {

  private static final ReentrantLock lock = new ReentrantLock();
  private static final Condition condition = lock.newCondition();

  public static void main(String[] args) {
    ReentrantCondition reentrantCondition = new ReentrantCondition();

    Thread producer = new Thread(() -> {
      try {
        reentrantCondition.produce();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Thread consumer = new Thread(() -> {
      try {
        reentrantCondition.consume();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    producer.start();
    consumer.start();
  }

  private void produce() throws InterruptedException {
    try {
      lock.lock();
      System.out.println("Inside Produce method...");
      /* if we invoke the await method without owning the lock, it throws IllegalMonitorStateException
      * awaitUnInterruptibly can't be interrupted. It makes the current thread wait until another thread signals it
      * Another waiting method is timed await. The current thread waits until it's signaled, interrupted or the specified time elapses
      * ex: await(1, TimeUnit.SECONDS)
      */
      condition.await();
      System.out.println("Inside Produce method again...");
    }finally {
      lock.unlock();
    }
  }

  private void consume() throws InterruptedException {
    try{
      lock.lock();
      System.out.println("Inside Consume method...");
      condition.signal();
      Thread.sleep(2000);
      System.out.println("Finished Consume method!!");
    }finally {
      lock.unlock();
    }
  }
}

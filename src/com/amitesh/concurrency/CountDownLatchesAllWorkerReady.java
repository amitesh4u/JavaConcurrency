package com.amitesh.concurrency;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * If we start thousands of threads, it’s likely that many of the earlier ones will have finished
 * processing before we have even called start() on the later ones. This could make it difficult to
 * try and reproduce a concurrency problem, as we would not be able to get all our threads to run in
 * parallel. To get around this, Instead of blocking a parent thread until some child threads have
 * finished, we can block each child thread until all the others have started.
 */

public class CountDownLatchesAllWorkerReady {

  public static void main(String[] args) {

    try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
      CountDownLatch readyThreadCounter = new CountDownLatch(5);
      CountDownLatch callingThreadBlocker = new CountDownLatch(1);
      CountDownLatch completedThreadCounter = new CountDownLatch(5);

      for (int i = 0; i < 5; i++) {
        executorService.execute(
            new WaitingWorker(i, readyThreadCounter, callingThreadBlocker, completedThreadCounter));
      }

      try {
        boolean allWorkersReady = readyThreadCounter.await(1, TimeUnit.SECONDS);
        System.out.println(STR."All Workers ready? \{allWorkersReady}");

        callingThreadBlocker.countDown();

        boolean completed = completedThreadCounter.await(10, TimeUnit.SECONDS);
        System.out.println(STR."All tasks completed? \{completed}");
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
      System.out.println("All the prerequisites are done...");
    }
  }

  private static class WaitingWorker implements Runnable {

    private final int id;
    private final CountDownLatch readyThreadCounter;
    private final CountDownLatch callingThreadBlocker;
    private final CountDownLatch completedThreadCounter;
    private final Random random;

    public WaitingWorker(int id, CountDownLatch readyThreadCounter,
        CountDownLatch callingThreadBlocker,
        CountDownLatch completedThreadCounter) {
      this.readyThreadCounter = readyThreadCounter;
      this.callingThreadBlocker = callingThreadBlocker;
      this.completedThreadCounter = completedThreadCounter;
      this.id = id;
      this.random = new Random();
    }

    public void run() {
      readyThreadCounter.countDown();
      try {
        callingThreadBlocker.await();
        doWork();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      } finally {
        completedThreadCounter.countDown();
      }
    }

    public void doWork() {
      try {
        System.out.println(STR."Thread with ID \{this.id} starts working...");
        Thread.sleep(this.random.nextInt(2000));
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }
}


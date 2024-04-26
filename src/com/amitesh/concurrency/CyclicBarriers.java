package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Latch --> multiple threads can wait for each other
 * <p>
 * A CyclicBarrier is used in situations where you want to create a group of tasks to perform work
 * in parallel + wait until they are all finished before moving on to the next step -> something
 * like join() -> something like CountDownLatch
 * <p>
 * CountDownLatch: one-shot event CyclicBarrier: it can be reused over and over again
 * <p>
 * + cyclicBarrier has a barrier action: a runnable, that will run automatically when the count
 * reaches 0 !!
 * <p>
 * new CyclicBarrier(N) -> N threads will wait for each other
 * <p>
 * WE CAN NOT REUSE LATCHES BUT WE CAN REUSE CyclicBarriers --> reset() !!!
 */


public class CyclicBarriers {
  private static final List<List<Integer>> PARTIAL_RESULTS = Collections.synchronizedList(new ArrayList<>());
  private static final int NUM_PARTIAL_RESULTS = 3;
  private static final int NUM_WORKERS = 5;


  public static void main(String[] args) {

    try (ExecutorService executorService = Executors.newFixedThreadPool(NUM_WORKERS)) {
      CyclicBarrier barrier = new CyclicBarrier(NUM_WORKERS, new AggregatorThread("Aggregator Thread") );

      for (int i = 0; i < NUM_WORKERS; i++) {
        executorService.execute(new Worker(i , barrier));
      }
    }
  }

  private record AggregatorThread(String id) implements Runnable {

    @Override
      public void run() {
        System.out.println(
            STR."\{id}: Computing final sum of \{NUM_WORKERS} workers, having \{NUM_PARTIAL_RESULTS} results each.");
        int sum = 0;
        for (List<Integer> threadResult : PARTIAL_RESULTS) {
          System.out.print("Adding ");
          for (Integer partialResult : threadResult) {
            System.out.print(STR."\{partialResult} ");
            sum += partialResult;
          }
          System.out.println();
        }
        System.out.println(STR."\{id}: Final result = \{sum}");
        System.out.println("We are able to use the Cyclic barrier...");
      }
    }

  private static class Worker implements Runnable {

    private final int id;
    private final Random random;
    private final CyclicBarrier cyclicBarrier;

    public Worker(int id, CyclicBarrier cyclicBarrier) {
      this.cyclicBarrier = cyclicBarrier;
      this.random = new Random();
      this.id = id;
    }

    @Override
    public void run() {
      doWork();
    }

    private void doWork() {
      System.out.println(STR."Thread with ID \{id} starts the task...");
      try {
        Thread.sleep(random.nextInt(3000));
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }

      List<Integer> partialResult = new ArrayList<>();
      for (int i = 0; i < NUM_PARTIAL_RESULTS; i++) {
        Integer num = random.nextInt(10);
        System.out.println(STR."\{id}: Crunching some numbers! Final result - \{num}");
        partialResult.add(num);
      }
      PARTIAL_RESULTS.add(partialResult);

      System.out.println(STR."Thread with ID \{id} finished...");

      try {
        System.out.println(STR."\{id} waiting for others to reach barrier.");
        cyclicBarrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        throw new RuntimeException();
      }
    }
  }
}


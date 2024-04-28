package com.amitesh.concurrency;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public class VirtualThreadExecutor {

  public static void main(String[] args) {
    System.out.println("~~~ Testing Virtual Thread with Non blocking calls ~~~\n");
    virtualThreadNotBlockedForResult();
    System.out.println("\n~~~ Testing Virtual Thread created with ThreadFactory with blocked calls along with result aggregation ~~~");
    virtualThreadResultAggregation();
  }

  private static void virtualThreadNotBlockedForResult() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 10).forEach(i -> executor.execute(() -> {
        try {
          Thread.sleep(Duration.ofSeconds(1));
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        Thread currentThread = Thread.currentThread();
        System.out.println(
            STR."Thread \{currentThread} with Id \{currentThread.threadId()} is printing \{
                i + 1} at \{new Date()}");
      }));
    }  // executor.close() is called implicitly, and waits
  }

  private static void virtualThreadResultAggregation() {

    Map<Integer, Future<Integer>> results = new HashMap<>(1000);
    ThreadFactory factory = Thread.ofVirtual().name("Test", 1).factory();
    try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
      IntStream.range(0, 10).forEach(i -> {
        Future<Integer> result = executor.submit(() -> {
          Thread.sleep(Duration.ofSeconds(1));
          Thread currentThread = Thread.currentThread();
          System.out.println(
              STR."Thread \{currentThread} with Id \{currentThread.threadId()} is printing \{
                  i + 1} at \{new Date()}");
          return i;
        });
        results.put(i, result);
      });
      int sum = results.values().stream().mapToInt(o -> {
        try {
          return o.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }).sum();
      System.out.println(STR."\nTotal sum of numbers 1-10 is \{sum}");
    }  // executor.close() is called implicitly, and waits
  }
}

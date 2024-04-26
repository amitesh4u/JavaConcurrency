package com.amitesh.concurrency;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class VirtualThread {

  public static void main(String[] args) {
    virtualThreadNotBlockedForResult();
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    virtualThreadResultAggregation();
    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    virtualThreadBlockedForResult();
  }

  private static void virtualThreadNotBlockedForResult() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 1_000).forEach(i -> executor.submit(() -> {
        Thread.sleep(Duration.ofSeconds(1));
        Thread currentThread = Thread.currentThread();
        System.out.println(
            STR."\{System.currentTimeMillis()} - \{currentThread.getName()} - \{currentThread.threadId()} - \{
                i + 1}");
        return i;
      }));
    }  // executor.close() is called implicitly, and waits
  }

  private static void virtualThreadResultAggregation() {

    Map<Integer, Future<Integer>> results = new HashMap<>(1000);

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 100).forEach(i -> {
        Future<Integer> result = executor.submit(() -> {
          Thread.sleep(Duration.ofSeconds(1));
          Thread currentThread = Thread.currentThread();
          System.out.println(
              STR."\{System.currentTimeMillis()} - \{currentThread.getName()} - \{currentThread.threadId()}");
          return i;
        });
        results.put(i, result);
      });
      System.out.println(results.values().stream().mapToInt(o -> {
        try {
          return o.get();
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      }).sum());
    }  // executor.close() is called implicitly, and waits
  }

  private static void virtualThreadBlockedForResult() {
    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
      IntStream.range(0, 10).forEach(i -> {
        try {
          int result = executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));
            Thread currentThread = Thread.currentThread();
            System.out.print(
                STR."\{System.currentTimeMillis()} - \{currentThread.getName()} - \{currentThread.threadId()}");
            return i;
          }).get();// This will block the execution till result is available
          System.out.println(STR." - \{result + 1}");
        } catch (InterruptedException | ExecutionException e) {
          throw new RuntimeException(e);
        }
      });
    }  // executor.close() is called implicitly, and waits
  }
}

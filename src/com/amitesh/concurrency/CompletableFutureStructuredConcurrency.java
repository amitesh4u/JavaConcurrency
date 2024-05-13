package com.amitesh.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public class CompletableFutureStructuredConcurrency {

  private static final int NUM_USERS = 2;

  public static void main(String[] args) {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

      IntStream.range(0, NUM_USERS).forEach(j ->
          executor.execute(
              () -> new CompletableFutureStructuredConcurrency().concurrentCallCompletableFuture(j))
      );
    }
  }

  private void concurrentCallCompletableFuture(int id) {
    System.out.println(STR."Executing process for User \{id}");
    ThreadFactory factory = Thread.ofVirtual().name("my-thread-", 1).factory();
    try (ExecutorService service = Executors.newThreadPerTaskExecutor(factory)) {

      String output = CompletableFuture
          .supplyAsync(this::dbCall, service)
          .thenCombine(
              CompletableFuture.supplyAsync(this::restCall, service)
              , (result1, result2) -> STR."[\{result1},\{result2}]")
          .thenApply(result -> {
            // both dbCall and restCall have completed
            String r = externalCall();
            return STR."[\{result},\{r}]";
          })
          .join(); // The Join (or get) will block the Virtual Thread but release the underlying Platform thread

      System.out.println(
          STR."Final concatenated result of all calls for User \{id} is \n \{output}");
    }
  }

  private String dbCall() {
    try {
      String databaseCall = "Database Call";
      NetworkCaller caller = new NetworkCaller(databaseCall);
      return databaseCall + caller.makeCall(2);
    } catch (Exception e) {
      return null;
    }
  }

  private String restCall() {
    try {
      String restApiCall = "REST API Call";
      NetworkCaller caller = new NetworkCaller(restApiCall);
      return restApiCall + caller.makeCall(5);
    } catch (Exception e) {
      return null;
    }
  }

  private String externalCall() {
    try {
      String externalCall = "External Call";
      NetworkCaller caller = new NetworkCaller(externalCall);
      return externalCall + caller.makeCall(4);
    } catch (Exception e) {
      return null;
    }
  }
}


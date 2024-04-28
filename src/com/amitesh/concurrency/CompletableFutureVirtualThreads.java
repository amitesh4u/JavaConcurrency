package com.amitesh.concurrency;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public class CompletableFutureVirtualThreads {

  private static final int NUM_USERS = 2;

  public static void main(String[] args) {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

      IntStream.range(0, NUM_USERS).forEach(j ->
          executor.execute(
              () -> new CompletableFutureVirtualThreads().concurrentCallCompletableFuture(j))
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

      System.out.println(STR."Final concatenated result of all calls is \{output}");
    }
  }

  private String dbCall() {
    try {
      NetworkCaller caller = new NetworkCaller("Database Call");
      return caller.makeCall(2);
    } catch (Exception e) {
      return null;
    }
  }

  private String restCall() {
    try {
      NetworkCaller caller = new NetworkCaller("REST API Call");
      return caller.makeCall(5);
    } catch (Exception e) {
      return null;
    }

  }

  private String externalCall() {
    try {
      NetworkCaller caller = new NetworkCaller("External Call");
      return caller.makeCall(4);
    } catch (Exception e) {
      return null;
    }
  }
}

class NetworkCaller {

  private final String callName;

  public NetworkCaller(String callName) {
    this.callName = callName;
  }

  public String makeCall(int secs) throws Exception {

    System.out.println(STR."Beginning \{callName} with Thread \{Thread.currentThread()}");

    try {
      URI uri = new URI(STR."http://httpbin.org/delay/\{secs}");
      try (InputStream stream = uri.toURL().openStream()) {
        return new String(stream.readAllBytes());
      }
    } finally {
      System.out.println(STR."Finished \{callName} with Thread \{Thread.currentThread()}");
    }

  }

}
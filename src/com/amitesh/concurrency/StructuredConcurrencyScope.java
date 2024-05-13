package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StructuredConcurrencyScope {

  private static final int NUM_USERS = 1;

  public static void main(String[] args) {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {

      IntStream.range(0, NUM_USERS).forEach(j ->
          executor.execute(() -> {
            try {
              concurrentCallStructuredScope(j);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          })
      );
    }
  }

  private static void concurrentCallStructuredScope(int id) throws InterruptedException {
    System.out.println(STR."Executing process for User \{id}");
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
      List<Subtask<String>> results = new ArrayList<>();

      results.add(scope.fork(StructuredConcurrencyScope::dbCall));
      results.add(scope.fork(StructuredConcurrencyScope::restCall));
      results.add(scope.fork(StructuredConcurrencyScope::externalCall));

      scope.join(); // The Join (or get) will block the Virtual Thread but release the underlying Platform thread
      String output = results.stream().map(Subtask::get).collect(Collectors.joining());
      System.out.println(
          STR."Final concatenated result of all calls for User \{id} is \n \{output}");
    }
  }

  private static String dbCall() {
    try {
      String databaseCall = "Database Call";
      NetworkCaller caller = new NetworkCaller(databaseCall);
      return databaseCall + caller.makeCall(2);
    } catch (Exception e) {
      return null;
    }
  }

  private static String restCall() {
    try {
      String restApiCall = "REST API Call";
      NetworkCaller caller = new NetworkCaller(restApiCall);
      return restApiCall + caller.makeCall(5);
    } catch (Exception e) {
      return null;
    }
  }

  private static String externalCall() {
    try {
      String externalCall = "External Call";
      NetworkCaller caller = new NetworkCaller(externalCall);
      return externalCall + caller.makeCall(4);
    } catch (Exception e) {
      return null;
    }
  }
}


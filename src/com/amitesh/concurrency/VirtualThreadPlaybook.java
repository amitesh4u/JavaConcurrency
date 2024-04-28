package com.amitesh.concurrency;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class VirtualThreadPlaybook {

  public static void main(String[] args) {
    usingThreadClass();
    completableFuture();
    virtualThreadPool();
    threadFactoryWithVirtualThread();
    virtualThreadGroup();
  }

  public static void usingThreadClass() {
    Thread virtualThread = Thread.startVirtualThread(() -> System.out.println(
            STR."Running task in a virtual thread: \{Thread.currentThread()}")
    );

    // Waiting for virtual threads to complete
    try {
      virtualThread.join();
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }
  }

  public static void completableFuture() {
    CompletableFuture<Void> future = CompletableFuture
        .supplyAsync(() -> "Virtual Thread")
        .thenApplyAsync(String::toUpperCase)
        .thenAcceptAsync(uppercaseResult -> System.out.println(
            STR."Uppercase result: \{uppercaseResult} in thread: \{Thread.currentThread()}"));

    future.join();
  }

  public static void virtualThreadPool() {
    try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
      for (int i = 0; i < 10; i++) {
        executor.submit(() -> System.out.println(
            STR."Running task in a virtual thread: \{Thread.currentThread()}"));
      }
    }
  }

  public static void threadFactoryWithVirtualThread() {
    ThreadFactory virtualThreadFactory = Thread.ofVirtual().factory();

    try (ExecutorService executor =
        Executors.newFixedThreadPool(8, virtualThreadFactory)) {

      for (int i = 0; i < 8; i++) {
        executor.submit(() -> System.out.println(
            STR."Running task in a virtual thread: \{Thread.currentThread()}"));
      }
    }
  }

  public static void virtualThreadGroup() {

    ThreadGroup virtualThreadGroup = Thread.currentThread().getThreadGroup();

    Thread virtualThread = new Thread(virtualThreadGroup, () -> System.out.println(
        STR."Running task in a virtual thread: \{Thread.currentThread()}"));

    virtualThread.start();
  }
}

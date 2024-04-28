package com.amitesh.concurrency;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceDemo {

  public static void main(String[] args) {
    System.out.println(STR."Started process at \{new Date()}");

    testCustomThreadPoolExecutor();
    testSingleThreadExecutor();
    testFixedPoolExecutor();
    testFixedPoolExecutorWithFactory();
    testCachedPoolExecutor();

    System.out.println(STR."Finished process at \{new Date()}");

  }

  private static void testCustomThreadPoolExecutor() {
    System.out.println(STR."Started Custom Thread Pool Executor at \{new Date()}");

    try (ExecutorService service = new ThreadPoolExecutor(2, 3, 0L, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<>())) {
      executeTasks(service);
    }

    System.out.println(STR."Finished Custom Thread Pool Executor at \{new Date()}\n");
  }

  private static void testSingleThreadExecutor() {
    System.out.println(STR."Started Single Thread Executor at \{new Date()}");

    try (ExecutorService service = Executors.newSingleThreadExecutor()) {
      executeTasks(service);
    }

    System.out.println(STR."Finished Single Thread Executor at \{new Date()}\n");
  }

  private static void testFixedPoolExecutor() {
    System.out.println(STR."Started Fixed Pool Thread Executor at \{new Date()}");

    try (ExecutorService service = Executors.newFixedThreadPool(2)) {
      executeTasks(service);
    }

    System.out.println(STR."Finished Fixed Pool Thread Executor at \{new Date()}\n");
  }

  private static void testFixedPoolExecutorWithFactory() {
    System.out.println(STR."Started Fixed Pool Thread Executor with Platform Thread Facotry at \{new Date()}");

    ThreadFactory factory = Thread.ofPlatform().name("MyThread", 1).factory();
    try (ExecutorService service = Executors.newFixedThreadPool(2, factory)) {
      executeTasks(service);
    }

    System.out.println(STR."Finished Fixed Pool Thread Executor with Platform Thread Factory at \{new Date()}\n");
  }


  private static void testCachedPoolExecutor() {
    System.out.println(STR."Started Cached Pool Thread Executor at \{new Date()}");

    try (ExecutorService service = Executors.newCachedThreadPool()) {
      executeTasks(service);
    }

    System.out.println(STR."Finished Cached Pool Thread Executor at \{new Date()}\n");
  }


  private static void executeTasks(ExecutorService service) {
    for (int i = 0; i < 5; i++) {
      int taskId = i + 1;
      service.execute(() -> new ExecutorServiceDemo().someTask(taskId));
    }
  }

  private void someTask(int taskId) {
    System.out.println(STR."Executing Task \{taskId} using Thread \{Thread.currentThread()
        .threadId()} at \{new Date()}");

    try {
      TimeUnit.SECONDS.sleep((long) (Math.random() * 3));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

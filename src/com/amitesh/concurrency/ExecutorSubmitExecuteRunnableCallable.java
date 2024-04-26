package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ExecutorSubmitExecuteRunnableCallable {

  public static void main(String[] args) throws ExecutionException, InterruptedException {

    testRunnableTask();

    testCallableTask();

    testAnyCallableTask();

    testAllCallableTasks();

    testCallableTaskCancellation();
  }

  private static void testRunnableTask() {
    try (ExecutorService runnableService = Executors.newFixedThreadPool(2)) {
      System.out.println("Invoking Runnable Task");
      for (int i = 1; i <= 5; i++) {
        runnableService.execute(new RunnableTask(String.valueOf(i)));
      }
      System.out.println("Finished Invoking Runnable Task");
    }
  }

  private static void testCallableTask() throws InterruptedException, ExecutionException {
    try (ExecutorService callableService = Executors.newFixedThreadPool(2)) {
      System.out.println("\nInvoking Callable Task");
      for (int i = 1; i <= 5; i++) {
        Future<String> future = callableService.submit(new CallableTask(String.valueOf(i)));
        System.out.println(STR."CallableTask Output \{future.get()}");
      }

      System.out.println("Finished Invoking Callable Task");
    }
  }

  private static void testAnyCallableTask() throws InterruptedException, ExecutionException {
    try (ExecutorService callableServiceInvokeAny = Executors.newFixedThreadPool(5)) {
      System.out.println("\nInvoking Any Callable Task");
      List<CallableTask> tasks = new ArrayList<>();
      for (int i = 1; i <= 5; i++) {
        CallableTask task = new CallableTask(String.valueOf(i));
        tasks.add(task);
      }
      String futureResult = callableServiceInvokeAny.invokeAny(tasks);
      System.out.println(STR."CallableTask Output \{futureResult}");
      System.out.println("Finished Invoking Any Callable Task");
    }
  }

  private static void testAllCallableTasks() throws InterruptedException {
    try (ExecutorService callableServiceInvokeAll = Executors.newFixedThreadPool(2)) {
      System.out.println("\nInvoking All Callable Tasks");
      List<CallableTask> tasks = new ArrayList<>();
      for (int i = 1; i <= 5; i++) {
        CallableTask task = new CallableTask(String.valueOf(i));
        tasks.add(task);
      }
      List<Future<String>> futureResults = callableServiceInvokeAll.invokeAll(tasks);
      futureResults.forEach(result -> {
        try {
          System.out.println(STR."CallableTask Output \{result.get(200, TimeUnit.MILLISECONDS)}");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
          throw new RuntimeException(e);
        }
      });

      System.out.println("Finished Invoking All Callable Tasks");
    }
  }


  private static void testCallableTaskCancellation() {
    try (ExecutorService callableService = Executors.newSingleThreadExecutor()) {
      System.out.println("\nInvoking Callable Task with Cancellation");
      Future<String> future = callableService.submit(new CallableTask(String.valueOf(100)));

      try {
        String result = future.get(500, TimeUnit.MILLISECONDS);
        System.out.println(STR."CallableTask Output \{result}");
      } catch (TimeoutException e) {
        System.out.println(STR."Has the Task been completed? \{future.isDone()}");
        future.cancel(true);
        boolean isCancelled = future.isCancelled();
        System.out.println(STR."Has the task been cancelled? \{isCancelled}");
      }

      System.out.println("Finished Invoking Callable Task");
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private record CallableTask(String taskId) implements Callable<String> {

    @Override
      public String call() throws Exception {
        TimeUnit.MILLISECONDS.sleep(1000);
        System.out.println(STR."Returning CallableTask Output \{taskId}");
        return STR."TaskId=\{taskId}";
      }
    }

  private record RunnableTask(String taskId) implements Runnable {

    @Override
      public void run() {
        try {
          TimeUnit.MILLISECONDS.sleep(1000);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        System.out.println(STR."Displaying RunnableTask Output \{taskId}");
      }
    }
}

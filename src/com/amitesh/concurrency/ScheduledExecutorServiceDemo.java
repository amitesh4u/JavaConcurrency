package com.amitesh.concurrency;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceDemo {

  public static void main(String[] args) {
    System.out.println(STR."Started process at \{new Date()}");

    //testScheduledExecutorInitialDelay();
    testScheduledExecutorFixedRateDelay();
    //testScheduledExecutorFixedTimeDelay();

    System.out.println(STR."Finished process at \{new Date()}");
  }

  /**
   * Submits a one-shot task that becomes enabled after the given delay.
   */
  private static void testScheduledExecutorInitialDelay() {
    System.out.println(STR."\nStarted Executor with Initial Delay at \{new Date()}");

    try (ScheduledExecutorService service = Executors.newScheduledThreadPool(1)) {
      service.schedule(() -> new ScheduledExecutorServiceDemo().someTask(1), 2, TimeUnit.SECONDS);
    }
    System.out.println(STR."Finished Executor with Initial Delay at \{new Date()}");
  }

  /**
   * The scheduleAtFixedRate() method lets us run a task periodically after a fixed delay.
   * If the processor needs more time to run an assigned task than the period parameter of the scheduleAtFixedRate() method,
   * the ScheduledExecutorService will wait until the current task is completed before starting the next.
   * i.e. New task will execute after Max(Period, Task Execution Time)
   */
  private static void testScheduledExecutorFixedRateDelay() {
    System.out.println(STR."\nStarted Scheduling Executor with Fixed Rate delay at \{new Date()}");

    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    service.scheduleAtFixedRate(() -> new ScheduledExecutorServiceDemo().someTask(2), 2, 3,
        TimeUnit.SECONDS);

    //service.close(); // Closing the Service will terminate the threads

    System.out.println(STR."Finished scheduling Executor with Fixed Rate delay at \{new Date()}");

    exitAfterDelay(service);
  }

  /**
   * Submits a periodic action that becomes enabled first after the given initial delay, and
   * subsequently with the given delay between the termination of one execution and the commencement
   * of the next. i.e. New task will execute after Delay + Execution Time
   */
  private static void testScheduledExecutorFixedTimeDelay() {
    System.out.println(STR."\nStarted Scheduling Executor with Fixed Time delay at \{new Date()}");

    ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
    service.scheduleWithFixedDelay(() -> new ScheduledExecutorServiceDemo().someTask(3), 2, 3,
        TimeUnit.SECONDS);
    // service.close(); // Closing the Service will terminate the threads

    System.out.println(STR."Finished scheduling Executor with Fixed Time delay at \{new Date()}");

    exitAfterDelay(service);
  }

  private static void exitAfterDelay(final ExecutorService executorService) {
    // Let the service run for given time
    try {
      TimeUnit.SECONDS.sleep(30);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    // Stop accepting new Tasks
    executorService.shutdown();
    try {
      // Wait for running tasks to finish within the given time
      if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
        // Destroy the service
        List<Runnable> pendingRunnables = executorService.shutdownNow();
        System.out.println(STR."Threads still running: \{pendingRunnables}");
      }
    } catch (InterruptedException e) {
      List<Runnable> pendingRunnables = executorService.shutdownNow();
      System.out.println(STR."Threads still running: \{pendingRunnables}");
    }
  }

  private void someTask(int taskId) {
    System.out.println(STR."Executing Task \{taskId} using Thread \{Thread.currentThread()
        .threadId()} at \{new Date()}");
    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}

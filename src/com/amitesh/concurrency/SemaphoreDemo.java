package com.amitesh.concurrency;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class SemaphoreDemo {

  private static final Semaphore semaphore = new Semaphore(3, true);

  public static void main(String[] args) {
    SemaphoreDemo semaphoreDemo = new SemaphoreDemo();
    try (ExecutorService executorService = Executors.newCachedThreadPool()) {
      for (int i = 0; i < 10; i++) {
        int processId = i;
        executorService.execute(() -> semaphoreDemo.processData(processId));
      }
    }

  }

  private void processData(int i) {
    try {
      semaphore.acquire();
      System.out.println(STR."Processing data \{i}");
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    semaphore.release();
  }


}

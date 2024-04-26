package com.amitesh.concurrency;

public class ThreadExceptionHandler {

  public static void main(String[] args) {
    Thread newThread = new Thread(() -> {
      Thread thread = Thread.currentThread();
      System.out.println(
          STR."Inside thread \{thread.getName()} with priority \{thread.getPriority()}");
      throw new RuntimeException("Manual exception");
    });

    newThread.setName("Test Thread");
    newThread.setPriority(Thread.NORM_PRIORITY);

    newThread.setUncaughtExceptionHandler((t , e) ->
      System.out.println(
          STR."Critical Exception thrown in Thread \{t.getName()}|Error=\{e.getMessage()}")
    );

    newThread.start();
  }
}

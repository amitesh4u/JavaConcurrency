package com.amitesh.concurrency;

public class DataRaceAndVolatile {

  private static final int LIMIT = 100000000;

  public static void main(String[] args) {
   // testNonVolatileSharedClass();
    testVolatileSharedClass();
  }

  private static void testNonVolatileSharedClass() {
    SharedClassNonVolatile sharedClassNonVolatile = new SharedClassNonVolatile();
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < LIMIT; i++) {
        sharedClassNonVolatile.increment();
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < LIMIT; i++) {
        sharedClassNonVolatile.checkForDataRace();
      }
    });

    thread1.start();
    thread2.start();
  }

  private static void testVolatileSharedClass() {
    SharedClassVolatile sharedClassVolatile = new SharedClassVolatile();
    Thread thread1 = new Thread(() -> {
      for (int i = 0; i < LIMIT; i++) {
        sharedClassVolatile.increment();
      }
    });

    Thread thread2 = new Thread(() -> {
      for (int i = 0; i < LIMIT; i++) {
        sharedClassVolatile.checkForDataRace();
      }
    });

    thread1.start();
    thread2.start();
  }
  public static class SharedClassNonVolatile {
    private int x = 0;
    private int y = 0;

    public void increment() {
      x++;
      y++;
    }

    public void checkForDataRace() {
      if (y > x) {
        System.out.println("y > x - Data Race is detected");
      }
    }
  }

  public static class SharedClassVolatile {
    private volatile int x = 0;
    private volatile int y = 0;

    public synchronized void increment() {
      x++;
      y++;
    }

    public void checkForDataRace() {
      if (y > x) {
        System.out.println("y > x - Data Race is detected");
      }
    }
  }

}

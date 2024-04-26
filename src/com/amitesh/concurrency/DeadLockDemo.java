package com.amitesh.concurrency;

import java.util.Random;

public class DeadLockDemo {

  public static void main(String[] args) throws InterruptedException {
    Intersection intersection = new Intersection();
    Thread trainAThread = new Thread(new TrainA(intersection), "Thread A");
    Thread trainBThread = new Thread(new TrainB(intersection), "Thread B");

    trainAThread.setDaemon(true);
    trainBThread.setDaemon(true);

    trainAThread.start();
    trainBThread.start();

    Thread.sleep(5000);

    System.exit(0);
  }

  public static class TrainA implements Runnable {

    private final Intersection intersection;
    private final Random random = new Random();

    public TrainA(Intersection intersection) {
      this.intersection = intersection;
    }

    @Override
    public void run() {
      while (true) {
        long sleepingTime = random.nextInt(5);
        try {
          Thread.sleep(sleepingTime);
        } catch (InterruptedException e) {
          System.out.println("Thread A is interrupted!!");
        }

        intersection.takeRoadA();
      }
    }
  }

  public static class TrainB implements Runnable {

    private final Intersection intersection;
    private final Random random = new Random();

    public TrainB(Intersection intersection) {
      this.intersection = intersection;
    }

    @Override
    public void run() {
      while (true) {
        long sleepingTime = random.nextInt(5);
        try {
          Thread.sleep(sleepingTime);
        } catch (InterruptedException e) {
          System.out.println("Thread B is interrupted!!");
        }

        intersection.takeRoadB();
        //intersection.takeRoadBFixed();
      }
    }
  }

  public static class Intersection {

    private final Object roadA = new Object();
    private final Object roadB = new Object();

    public void takeRoadA() {
      synchronized (roadA) {
        System.out.println(STR."Road A is locked by thread \{Thread.currentThread().getName()}");

        synchronized (roadB) {
          System.out.println("Train is passing through road A");
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            // Ignore
          }
        }
      }
    }

    public void takeRoadB() {
      synchronized (roadB) {
        System.out.println(STR."Road B is locked by thread \{Thread.currentThread().getName()}");

        synchronized (roadA) {
          System.out.println("Train is passing through road B");

          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            // Ignore
          }
        }
      }
    }

    public void takeRoadBFixed() {
      synchronized (roadA) { // Change the lock order to same pattern RoadA followed by RoadB everywhere to fix

        synchronized (roadB) {
          System.out.println(STR."Road B is locked by thread \{Thread.currentThread().getName()}");
          System.out.println("Train is passing through road B");

          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            // Ignore
          }
        }
      }
    }
  }
}

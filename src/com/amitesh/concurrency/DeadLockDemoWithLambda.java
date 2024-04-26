package com.amitesh.concurrency;

import java.util.Random;

public class DeadLockDemoWithLambda {

  private static final Random RANDOM = new Random();
  private static final Intersection INTERSECTION = new Intersection();

  public static void main(String[] args) throws InterruptedException {
    DeadLockDemoWithLambda deadLockDemo = new DeadLockDemoWithLambda();

    new Thread(deadLockDemo::runTrainA, "Thread A").start();
    new Thread(deadLockDemo::runTrainB, "Thread B").start();

    Thread.sleep(5000);

    System.exit(0);
  }

  private void runTrainA(){
    while (true) {
      long sleepingTime = RANDOM.nextInt(5);
      try {
        Thread.sleep(sleepingTime);
      } catch (InterruptedException e) {
        System.out.println("Thread A is interrupted!!");
      }

      INTERSECTION.takeRoadB();
      //INTERSECTION.takeRoadBFixed();
    }
  }

  private void runTrainB(){
    while (true) {
      long sleepingTime = RANDOM.nextInt(5);
      try {
        Thread.sleep(sleepingTime);
      } catch (InterruptedException e) {
        System.out.println("Thread A is interrupted!!");
      }

      INTERSECTION.takeRoadA();
    }
  }
}

class Intersection {

  private final Object roadA = new Object();
  private final Object roadB = new Object();

  public void takeRoadA() {
    synchronized (roadA) {
      System.out.println(STR."Road A is locked by \{Thread.currentThread().getName()}");

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
      System.out.println(STR."Road B is locked by \{Thread.currentThread().getName()}");

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
        System.out.println(STR."Road B is locked by \{Thread.currentThread().getName()}");
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

package com.amitesh.concurrency;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
 * With the help of Exchanger -> two threads can exchange objects
 * <p>
 * exchange() -> exchanging objects is done via one of the two exchange() methods
 * <p>
 * For example: genetic algorithms, training neural networks
 */

class Exchangers {

  public static void main(String[] args) throws InterruptedException {

    Exchanger<Integer> exchanger = new Exchanger<>();

    new Thread(new FirstWorker(exchanger)).start();
    new Thread(new SecondWorker(exchanger)).start();

    TimeUnit.SECONDS.sleep(3);

    System.exit(0);
  }
}

class FirstWorker implements Runnable {

  private final Exchanger<Integer> exchanger;
  private int counter;

  public FirstWorker(Exchanger<Integer> exchanger) {
    this.exchanger = exchanger;
  }

  @Override
  public void run() {

    while (true) {

      counter = counter + 1;
      System.out.println(STR."FirstWorker incremented the counter: \{counter}");

      try {
        counter = exchanger.exchange(counter);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }
}

class SecondWorker implements Runnable {

  private final Exchanger<Integer> exchanger;
  private int counter;

  public SecondWorker(Exchanger<Integer> exchanger) {
    this.exchanger = exchanger;
  }

  @Override
  public void run() {

    while (true) {

      counter = counter - 1;
      System.out.println(STR."SecondWorker decremented the counter: \{counter}");

      try {
        counter = exchanger.exchange(counter);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }
}


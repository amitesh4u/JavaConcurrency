package com.amitesh.concurrency;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The Dining Philosopher Problem states that K philosophers are seated around a circular table with
 * one chopstick between each pair of philosophers. There is one chopstick between each philosopher.
 * A philosopher may eat if he can pick up the two chopsticks adjacent to him. One chopstick may be
 * picked up by any one of its adjacent followers but not both.
 * <p>
 * The goal is to come up with a scheme/protocol that helps the philosophers achieve
 * their goal of eating and thinking without getting starved to death.
 * Approach: To allow everyone to eat make sure
 * -- to not block one chopstick with any diner if second chopstick is not available
 * -- to Think and Eat for random durations
 * -- to always pick the chopsticks in same order i.e. first left then right or vice versa
 */

public class DiningPhilosopherProblem {

  private static final int NO_OF_PHILOSOPHERS = 7;
  private static final int NO_OF_CHOPSTICKS = NO_OF_PHILOSOPHERS;

  public static void main(String[] args) throws InterruptedException {
    ChopStick[] chopsticks = new ChopStick[NO_OF_CHOPSTICKS];
    for (int i = 0; i < chopsticks.length; i++) {
      chopsticks[i] = new ChopStick(i);
    }

    try (ExecutorService philosophers = Executors.newFixedThreadPool(NO_OF_PHILOSOPHERS)) {
      for (int i = 0; i < NO_OF_PHILOSOPHERS; i++) {
        ChopStick rightChopstick = chopsticks[i];
        ChopStick leftChopstick = chopsticks[(i + 1) % NO_OF_CHOPSTICKS];
        Philosopher philosopher = new Philosopher(String.valueOf(i), leftChopstick, rightChopstick);
        System.out.println(
            STR."\{philosopher} has \{rightChopstick} in his Right and \{leftChopstick} in his Left side.");
        philosophers.execute(philosopher);
      }
    }
  }
}

class Philosopher implements Runnable {

  private static final int NO_OF_ALLOWED_SERVINGS = 4;
  private static final Random RANDOM = new Random();
  private final String id;
  private final ChopStick leftChopstick;
  private final ChopStick rightChopstick;
  private int noOfServings = 0;

  public Philosopher(String id, ChopStick leftChopstick, ChopStick rightChopstick) {
    this.id = id;
    this.leftChopstick = leftChopstick;
    this.rightChopstick = rightChopstick;
  }

  @Override
  public void run() {

    while (true) {
      try {
        System.out.println(STR."\{this} is trying to acquire Left \{leftChopstick}.");
        if (leftChopstick.pickUp()) {
          System.out.println(STR."\{this} has acquired Left \{leftChopstick}.");
          try {
            System.out.println(STR."\{this} is trying to acquire Right \{rightChopstick}.");
            if (rightChopstick.pickUp()) {
              System.out.println(STR."\{this} has acquired Right \{rightChopstick}.");

              noOfServings++;
              System.out.println(STR."\{this} is eating its \{noOfServings} serving!!");

              Thread.sleep(RANDOM.nextInt(1000));
              System.out.println(STR."\{this} is releasing Right \{rightChopstick}.");
              rightChopstick.putDown();
            } else {
              System.out.println(STR."\{this} has failed to acquire Right \{rightChopstick}.");
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          System.out.println(STR."\{this} is releasing Left \{leftChopstick}.");
          leftChopstick.putDown();
        } else {
          System.out.println(
              STR."\{this} has failed to acquire Left \{leftChopstick}. Will try after some Think time.");
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (noOfServings == NO_OF_ALLOWED_SERVINGS) {
        System.out.println(STR."\{this} is done with eating.");
        break;
      }
      try {
        System.out.println(STR."\{this} is thinking.");
        Thread.sleep(RANDOM.nextInt(1000));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public String toString() {
    return STR."Philosopher-\{id}";
  }
}

class ChopStick {

  private final Lock lock;
  private final int id;

  public ChopStick(int id) {
    this.id = id;
    this.lock = new ReentrantLock();
  }

  public boolean pickUp() throws InterruptedException {
    return this.lock.tryLock(1000, TimeUnit.MILLISECONDS);
  }

  public void putDown() {
    this.lock.unlock();
  }

  @Override
  public String toString() {
    return STR."Chopstick-\{this.id}";
  }
}


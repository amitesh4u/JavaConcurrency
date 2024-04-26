package com.amitesh.concurrency;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The Simplified Dining Problem There are K diners seated around a table with M forks on the table.
 * A Diner may eat if he can pick up any two chopsticks. He can have N servings of Food after that
 * he will be marked Full. Once All diners are done eating the simulation will end. When a Diner is
 * not eating he will spend his time Thinking (a better solution???)
 * <p>
 * Approach: To allow everyone to eat make sure
 * -- to not block one fork with any diner if second fork is not available
 * -- to Think and Eat for random durations
 * -- to always pick the forks in same order i.e. first left then right or vice versa
 */
public class DinersProblemSimplified {

  private static final int NO_OF_DINERS = 7;
  private static final int NO_OF_CHOPSTICKS = 5; // Making it bit trickier :)

  public static void main(String[] args) throws InterruptedException {
    Semaphore forks = new Semaphore(NO_OF_CHOPSTICKS);

    try (ExecutorService diners = Executors.newFixedThreadPool(NO_OF_DINERS)) {
      for (int i = 1; i <= NO_OF_DINERS; i++) {
        diners.execute(new Diner(String.valueOf(i), forks));
      }
    }
  }
}

class Diner implements Runnable {

  private static final int NO_OF_ALLOWED_SERVINGS = 4;
  private static final Random RANDOM = new Random();
  private final String id;
  private final Semaphore forks;
  private int noOfServings = 0;

  public Diner(String id, Semaphore forks) {
    this.id = id;
    this.forks = forks;
  }

  @Override
  public void run() {

    while (true) {
      try {
        System.out.println(STR."\{this} is trying to acquire Left Fork.");
        if (forks.tryAcquire(RANDOM.nextInt(1000), TimeUnit.MILLISECONDS)) {
          System.out.println(STR."\{this} has acquired Left Fork.");
          try {
            System.out.println(STR."\{this} is trying to acquire Right Fork.");
            if (forks.tryAcquire(RANDOM.nextInt(1000), TimeUnit.MILLISECONDS)) {
              System.out.println(STR."\{this} has acquired Right Fork.");

              noOfServings++;
              System.out.println(STR."\{this} is eating its \{noOfServings} serving!!");

              Thread.sleep(RANDOM.nextInt(1000));
              System.out.println(STR."\{this} is releasing Right Fork.");
              forks.release();
            } else {
              System.out.println(STR."\{this} has failed to acquire Right Fork.");
            }
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          System.out.println(STR."\{this} is releasing Left Fork.");
          forks.release();
        } else {
          System.out.println(
              STR."\{this} has failed to acquire Left Fork. Will try after some Think time.");
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
  public String toString(){
    return STR."Diner-\{id}";
  }
}

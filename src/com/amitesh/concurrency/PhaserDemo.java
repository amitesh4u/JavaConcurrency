package com.amitesh.concurrency;

import java.util.concurrent.Phaser;

/**
 * Phaser is a very similar construct to the CountDownLatch. The Phaser is a barrier on which the
 * dynamic number of threads need to wait before continuing execution. To participate in the
 * coordination, the thread needs to register() itself with the Phaser instance. The thread signals
 * that it arrived at the barrier by calling the arriveAndAwaitAdvance(), which is a blocking
 * method. When the number of arrived parties is equal to the number of registered parties, the
 * execution of the program will continue, and the phase number will increase. We can get the
 * current phase number by calling the etPhase() method. When the thread finishes its job, we should
 * call the arriveAndDeregister() method to signal that the current thread should no longer be
 * accounted for in this particular phase.
 */
public class PhaserDemo {

  public static void main(String[] args) throws InterruptedException {

    /*
     * Passing one as argument is equivalent to calling the register() method from the current thread.
     * We’re doing this because, when we’re creating three worker threads,
     * the main thread is a coordinator, and therefore the Phaser needs
     * to have four threads registered to it
     */
    Phaser ph = new Phaser(1);

    /* The phase after the initialization is equal to zero. */
    System.out.println(STR."Phase before starting is \{ph.getPhase()}");

    /*
     * We’ve initialized our Phaser with 1 and called register() three more times.
     */
    new Thread(new LongRunningAction("thread-1", ph)).start();
    new Thread(new LongRunningAction("thread-2", ph)).start();
    new Thread(new LongRunningAction("thread-3", ph)).start();

    String threadName = Thread.currentThread().getName();

    /*
     * Three action threads have announced that they’ve arrived at the barrier,
     * so one more call of arriveAndAwaitAdvance() is needed – the one from the main thread.
     */
    System.out.println(STR."Thread \{threadName} waiting for others");
    ph.arriveAndAwaitAdvance();

    /*
     * After the completion of that phase, the getPhase() method will return one
     * because the program finished processing the first step of execution
     */
    System.out.println(
        STR."Thread \{threadName} proceeding in phase \{ph.getPhase()}");
    System.out.println(STR."Phase after first call is \{ph.getPhase()}");

    /* We are reusing the same phaser to register more threads */
    new Thread(new LongRunningAction("thread-4", ph)).start();
    new Thread(new LongRunningAction("thread-5", ph)).start();

    /* After the call from main, phaser will complete phase two */
    System.out.println(STR."Thread \{threadName} waiting for others");
    ph.arriveAndAwaitAdvance();
    System.out.println(
        STR."Thread \{threadName} proceeding in phase \{ph.getPhase()}");
    System.out.println(STR."Phase after first call is \{ph.getPhase()}");

    /* To finish our program, we need to call the arriveAndDeregister() method
     * as the main thread is still registered in the Phaser. When the deregistration
     * causes the number of registered parties to become zero, the Phaser is terminated.
     */
    ph.arriveAndDeregister();
    Thread.sleep(1000);
    System.out.println(STR."Phase terminated? \{ph.isTerminated()}");

  }

  private record LongRunningAction(String threadName, Phaser ph) implements Runnable {

    private LongRunningAction(String threadName, Phaser ph) {
      this.threadName = threadName;
      this.ph = ph;

      this.randomWait();

      /* To participate in the coordination, the thread needs to register() itself with the Phaser instance.*/
      ph.register();
      System.out.println(STR."Thread \{threadName} registered during phase \{ph.getPhase()}");
    }

    @Override
    public void run() {
      System.out.println(
          STR."Thread \{threadName} BEFORE long running action in phase \{ph.getPhase()}");
      /* The thread signals that it arrived at the barrier by calling the arriveAndAwaitAdvance(),
       * which is a blocking method
       */
      ph.arriveAndAwaitAdvance();

      randomWait();

      System.out.println(
          STR."Thread \{threadName} AFTER long running action in phase \{ph.getPhase()}");
      /*
       * Should call the arriveAndDeregister() method to signal that the current
       * thread should no longer be accounted for in this particular phase
       */
      ph.arriveAndDeregister();
    }

    /* Simulating real work */
    private void randomWait() {
      try {
        Thread.sleep((long) (Math.random() * 100));
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }

}

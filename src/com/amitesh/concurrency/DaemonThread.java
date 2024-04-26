package com.amitesh.concurrency;

import java.math.BigInteger;

/**
 * Daemon threads are threads that run in the background and do not prevent our application from exiting.
 *  Daemon Thread are terminated by the JVm when all other worker Threads finish execution
 *  whereas Worker threads (not daemon) are not terminated when Daemon threads are interrupted
 * <p>
 * For example, in the text editor application, we have a thread that saves
 * our work every few minutes to a file. If suddenly we want to close our
 * application, we don't really care that this background thread is still running,
 * and we don't want to wait for it to finish.
 *<p>
 * A second scenario is when we do not have control over the code, being
 * executed in the worker thread. And we want to make sure that it will not
 * become a blocker for us to stop the app. A good example of such
 * a case can be some code that's calling some external libraries that might not
 * handle the threat interrupt.
 */
public class DaemonThread {

  public static void main(String[] args) {
    long maxWaitingTime = 5000;
    long startTime = System.currentTimeMillis();

    Thread slowThread = new Thread(
        new LongComputationTask(new BigInteger("200000"), new BigInteger("100000000")));
    slowThread.setName("Slow Thread");

    Thread fastThread = new Thread(
        new LongComputationTask(new BigInteger("300000"), new BigInteger("500")));
    fastThread.setName("Fast Thread");

    slowThread.setDaemon(true);
    fastThread.setDaemon(false);


    System.out.println(STR."Slow Thread is Daemon? \{slowThread.isDaemon()}");
    System.out.println(STR."Fast Thread is Daemon? \{fastThread.isDaemon()}");

    slowThread.start();
    fastThread.start();

    while (true) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - startTime > maxWaitingTime) {
        slowThread.interrupt();
        System.out.println("Interrupting slow Thread!!");
        //fastThread.interrupt();
        break;
      }
    }
  }

  private record LongComputationTask(BigInteger base, BigInteger power) implements Runnable {

    @Override
    public void run() {
      System.out.println(STR."\{base}^\{power} = \{pow(base, power)}");
    }

    private BigInteger pow(BigInteger base, BigInteger power) {
      BigInteger result = BigInteger.ONE;

      for (BigInteger i = BigInteger.ZERO; i.compareTo(power) != 0; i = i.add(BigInteger.ONE)) {
        result = result.multiply(base);
      }

      return result;
    }
  }

}

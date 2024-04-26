package com.amitesh.concurrency;

/**
 * Case 1: Daemon Thread are terminated by the JVm when all other Worker Threads finish execution whereas
 * Case 2: Worker threads (not daemon) are not terminated when Daemon threads are interrupted
 */
public class DaemonNormalThread {

  public static void main(String[] args) {

    //testDaemonThreadTermination();

    testDaemonThreadInterruption();
  }

  private static void testDaemonThreadInterruption() {
    Thread normalThread = new NormalThread();
    Thread daemonThread = new DaemonThread();

    normalThread.setDaemon(false);
    daemonThread.setDaemon(true);

    normalThread.start();
    daemonThread.start();

    try {
      Thread.sleep(2000);
      daemonThread.interrupt();
    } catch (InterruptedException e) {
      // Ignore
    }


  }

  private static void testDaemonThreadTermination() {
    Thread normalThread = new NormalThread();
    Thread daemonThread = new DaemonThread();

    normalThread.setDaemon(false);
    daemonThread.setDaemon(true);

    normalThread.start();
    daemonThread.start();
  }


  private static class NormalThread extends Thread {

    @Override
    public void run() {
      System.out.println("Normal Thread is running...");
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        System.out.println("Normal thread has been interrupted!!");
      }
      System.out.println("Normal Thread finished!!");
    }
  }

  private static class DaemonThread extends Thread {

    @Override
    public void run() {
      while (true) {
        try {
          Thread.sleep(1000);
          System.out.println("Daemon Thread is running...");
        } catch (InterruptedException e) {
          System.out.println("Daemon thread has been interrupted!!");
          break;
        }
      }
    }
  }
}

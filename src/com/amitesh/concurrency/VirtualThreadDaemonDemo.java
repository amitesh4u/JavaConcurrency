package com.amitesh.concurrency;

import java.lang.Thread.Builder.OfVirtual;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

public class VirtualThreadDaemonDemo {

  private static final int NUM_THREADS = 100;
  private static final boolean SHOULD_WAIT = true;

  /* Create a Thread factory with Thread names as Prefix+Counter starting from given value. Here Test1, Test2 */
  private static final ThreadFactory THREAD_FACTORY;

  static {
    OfVirtual threadBuilder = Thread.ofVirtual().name("Test", 1); // Not Thread Safe
    THREAD_FACTORY = threadBuilder.factory(); // Thread Safe. Use this
  }

  public static void main(String[] args) throws Exception {

    System.out.println(
        STR."Starting main \{Thread.currentThread()} and trying to execute \{NUM_THREADS} threads in \{
            5 * NUM_THREADS} milliseconds");

    var threads = new ArrayList<Thread>();
    for (int i = 0; i < NUM_THREADS; i++) {
      threads.add(startThread());
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }

    if (SHOULD_WAIT) {
      // join on the threads
      for (Thread thread : threads) {
        thread.join();
      }
    }

    System.out.println("Ending main");
  }

  private static Thread startThread() {
    Thread thread = THREAD_FACTORY.newThread(VirtualThreadDaemonDemo::handleUserRequest);
    thread.start();
    return thread;

    // Create and Start a new Virtual thread. No name is associated with thread
    //return Thread.startVirtualThread(VirtualThreadDaemonDemo::handleUserRequest);
  }

  private static void handleUserRequest() {
    System.out.println(
        STR."Starting thread \{Thread.currentThread()} with id \{Thread.currentThread()
            .threadId()}");

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      throw new RuntimeException();
    }

    /* If we don't wait for all the threads to complete and terminate the Main threads then this line will never execute */
    System.out.println(
        STR."Ending thread \{Thread.currentThread()} with id \{Thread.currentThread().threadId()}");
  }
}

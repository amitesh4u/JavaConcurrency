package com.amitesh.concurrency;

public class WaitNotify {

  public static void main(String[] args) {
    WaitNotify waitNotify = new WaitNotify();

    Thread producer = new Thread(() -> {
      try {
        waitNotify.produce();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    Thread consumer = new Thread(() -> {
      try {
        waitNotify.consume();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });

    producer.start();
    consumer.start();
  }

  private void produce() throws InterruptedException {
    synchronized (this){ // WaitNotify object intrinsic lock
      System.out.println("Inside Produce method...");
      wait();
      System.out.println("Inside Produce method again...");
    }
  }

  private void consume() throws InterruptedException {
    synchronized (this){ // WaitNotify object intrinsic lock
      System.out.println("Inside Consume method...");
      notify();
      Thread.sleep(3000);
      System.out.println("Finished Consume method!!");
    }
  }
}

package com.amitesh.concurrency;

public class InventoryCounterSynchronization {

  public static void main(String[] args) throws InterruptedException {
    InventoryCounter inventoryCounter = new InventoryCounter();
    IncrementingThread incrementingThread = new IncrementingThread(inventoryCounter);
    DecrementingThread decrementingThread = new DecrementingThread(inventoryCounter);

    incrementingThread.start();
    decrementingThread.start();

    incrementingThread.join();
    decrementingThread.join();

    System.out.println(STR."We currently have \{inventoryCounter.getItems()} items");
  }

  public static class DecrementingThread extends Thread {

    private final InventoryCounter inventoryCounter;

    public DecrementingThread(InventoryCounter inventoryCounter) {
      this.inventoryCounter = inventoryCounter;
    }

    @Override
    public void run() {
      for (int i = 0; i < 10000; i++) {
        inventoryCounter.decrement();
      }
    }
  }

  public static class IncrementingThread extends Thread {

    private final InventoryCounter inventoryCounter;

    public IncrementingThread(InventoryCounter inventoryCounter) {
      this.inventoryCounter = inventoryCounter;
    }

    @Override
    public void run() {
      for (int i = 0; i < 10000; i++) {
        inventoryCounter.increment();
      }
    }
  }

 public static class InventoryCounter {

    private int items = 0;

    final Object lock = new Object();

    public void increment() {
      synchronized (this.lock) {
        items++;
      }
    }

    public void decrement() {
      synchronized (this.lock) {
        items--;
      }
    }

    public int getItems() { // int getter is an inherent atomic operation so sync is not req
        return items;
    }
  }
}

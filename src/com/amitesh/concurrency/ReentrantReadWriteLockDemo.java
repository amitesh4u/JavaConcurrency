package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReentrantReadWriteLockDemo {

  public static final int HIGHEST_PRICE = 1000;

  public static void main(String[] args) throws InterruptedException {
    InventoryDatabase inventoryDatabase = new InventoryDatabase();

    Random random = new Random();
    for (int i = 0; i < 100000; i++) {
      inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
    }

    Thread writer = new Thread(() -> {
      while (true) {
        inventoryDatabase.addItem(random.nextInt(HIGHEST_PRICE));
        inventoryDatabase.removeItem(random.nextInt(HIGHEST_PRICE));
        try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          // Ignore
        }
      }
    });

    writer.setDaemon(true);
    writer.start();

    int numberOfReaderThreads = 7;
    List<Thread> readers = new ArrayList<>();

    for (int readerIndex = 0; readerIndex < numberOfReaderThreads; readerIndex++) {
      Thread reader = new Thread(() -> {
        for (int i = 0; i < 100000; i++) {
          int upperBoundPrice = random.nextInt(HIGHEST_PRICE);
          int lowerBoundPrice = upperBoundPrice > 0 ? random.nextInt(upperBoundPrice) : 0;
          inventoryDatabase.getNumberOfItemsInPriceRange(lowerBoundPrice, upperBoundPrice);
        }
      });

      reader.setDaemon(true);
      readers.add(reader);
    }

    long startReadingTime = System.currentTimeMillis();
    for (Thread reader : readers) {
      reader.start();
    }

    for (Thread reader : readers) {
      reader.join();
    }

    long endReadingTime = System.currentTimeMillis();

    System.out.printf("Reading took %d ms%n", endReadingTime - startReadingTime);
  }

  public static class InventoryDatabase {

    private final TreeMap<Integer, Integer> priceToCountMap = new TreeMap<>();
    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = reentrantReadWriteLock.readLock();
    private final Lock writeLock = reentrantReadWriteLock.writeLock();

    public void getNumberOfItemsInPriceRange(int lowerBound, int upperBound) {
      readLock.lock();
      try {
        Integer fromKey = priceToCountMap.ceilingKey(lowerBound);

        Integer toKey = priceToCountMap.floorKey(upperBound);

        if (fromKey == null || toKey == null) {
          return;
        }

        NavigableMap<Integer, Integer> rangeOfPrices = priceToCountMap.subMap(fromKey, true, toKey,
            true);

        int sum = 0;
        for (int numberOfItemsForPrice : rangeOfPrices.values()) {
          sum += numberOfItemsForPrice;
        }

      } finally {
        readLock.unlock();
      }
    }

    public void addItem(int price) {
      writeLock.lock();
      try {
        priceToCountMap.merge(price, 1, Integer::sum);

      } finally {
        writeLock.unlock();
      }
    }

    public void removeItem(int price) {
      writeLock.lock();
      try {
        Integer numberOfItemsForPrice = priceToCountMap.get(price);
        if (numberOfItemsForPrice == null || numberOfItemsForPrice == 1) {
          priceToCountMap.remove(price);
        } else {
          priceToCountMap.put(price, numberOfItemsForPrice - 1);
        }
      } finally {
        writeLock.unlock();
      }
    }
  }

}

package com.amitesh.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentMaps {

  public static void main(String[] args) {

    ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

    ProducerWorker producerWorker = new ProducerWorker(map);
    ConsumerWorker secondWorker = new ConsumerWorker(map);

    new Thread(producerWorker).start();
    new Thread(secondWorker).start();

  }

  static class ProducerWorker implements Runnable {

    private final ConcurrentMap<String, Integer> map;

    public ProducerWorker(ConcurrentMap<String, Integer> map) {
      this.map = map;
    }

    @Override
    public void run() {
      try {
        map.put("B", 1);
        map.put("H", 2);
        map.put("F", 3);
        Thread.sleep(1000);
        map.put("A", 4);
        Thread.sleep(1000);
        map.put("E", 5);
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }


  static class ConsumerWorker implements Runnable {

    private final ConcurrentMap<String, Integer> map;

    public ConsumerWorker(ConcurrentMap<String, Integer> map) {
      this.map = map;
    }

    @Override
    public void run() {
      try {
        Thread.sleep(5000);
        System.out.println(map.get("A"));
        Thread.sleep(1000);
        System.out.println(map.get("E"));
        Thread.sleep(1000);
        System.out.println(map.get("C"));
      } catch (InterruptedException e) {
        throw new RuntimeException();
      }
    }
  }

}

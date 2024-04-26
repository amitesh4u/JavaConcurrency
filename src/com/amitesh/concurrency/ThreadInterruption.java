package com.amitesh.concurrency;

import java.math.BigInteger;

public class ThreadInterruption {

  public static void main(String[] args) {
    long maxWaitingTime = 5000;
    long startTime = System.currentTimeMillis();

    Thread slowThread = new Thread(
        new LongComputationTask(new BigInteger("200000"), new BigInteger("100000000")));
    slowThread.setName("Slow Thread");

    Thread fastThread = new Thread(
        new LongComputationTask(new BigInteger("300000"), new BigInteger("500")));
    fastThread.setName("Fast Thread");

    slowThread.start();
    fastThread.start();

    while (true) {
      long currentTime = System.currentTimeMillis();
      if (currentTime - startTime > maxWaitingTime) {
        slowThread.interrupt();
        fastThread.interrupt();
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
        if (Thread.currentThread().isInterrupted()) {
          System.out.println(
              STR."Prematurely interrupted computation for \{Thread.currentThread().getName()}");
          System.out.println(STR."current result: \{base}^\{i}");
          return BigInteger.ZERO;
        }
        result = result.multiply(base);
      }

      return result;
    }
  }
}

package com.amitesh.concurrency;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreadJoining {

  public static void main(String[] args) throws InterruptedException {
    List<Long> inputNumbers = Arrays.asList(100000000L, 343L, 543L, 224L, 456L, 23L);

    List<FactorialThread> threads = new ArrayList<>();

    for (long inputNumber : inputNumbers) {
      threads.add(new FactorialThread(inputNumber));
    }

    for (Thread thread : threads) {
      thread.setDaemon(true); // Let main Thread complete even when the thread is running
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join(2000); // Wait for the Thread to terminate for max 2 sec then leave. Don't wait for it to finish
    }

    for (int i = 0; i < inputNumbers.size(); i++) {
      FactorialThread factorialThread = threads.get(i);
      if (factorialThread.isFinished()) {
        System.out.println(
            STR."Factorial of \{inputNumbers.get(i)} is \{factorialThread.getResult()}");
      } else {
        System.out.println(STR."The calculation for \{inputNumbers.get(i)} is still in progress");
      }
    }
  }

  public static class FactorialThread extends Thread {

    private final long inputNumber;
    private BigInteger result = BigInteger.ZERO;
    private boolean isFinished = false;

    public FactorialThread(long inputNumber) {
      this.inputNumber = inputNumber;
    }

    @Override
    public void run() {
      this.result = factorial(inputNumber);
      this.isFinished = true;
    }

    public BigInteger factorial(long n) {
      BigInteger tempResult = BigInteger.ONE;

      for (long i = n; i > 0; i--) {
        tempResult = tempResult.multiply(new BigInteger((Long.toString(i))));
      }
      return tempResult;
    }

    public BigInteger getResult() {
      return result;
    }

    public boolean isFinished() {
      return isFinished;
    }
  }
}

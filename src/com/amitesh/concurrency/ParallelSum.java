package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ParallelSum {

  public static void main(String[] args) {

    int[] numbers = new int[100];
    Random random = new Random();
    for (int i = 0; i < numbers.length; i++) {
      numbers[i] = random.nextInt(101) + 1; // 1..100
      System.out.print(STR."\{numbers[i]} ");
    }
    System.out.println("\n");

    int noOfThreads = Runtime.getRuntime().availableProcessors();

    int subArrSize = (int) Math.ceil((double) numbers.length / noOfThreads);
    /* The adjustment of Thread count is req in scenarios where due to Ceil value
     * an unnecessary Thread is used. Ex: If Array size is 100 and original Thread count is 16
     * then sub array size will be 7 due to ceil (100/16 = 6.25).
     * We can easily divide 100 numbers in 7 parts with 15 Threads only 15 * 7 = 105.
     * The 16th Thread is unnecessary.
     */
    noOfThreads = (int) Math.ceil((double) numbers.length / subArrSize);

    List<Future<Long>> partialResults = new ArrayList<>();

    try (ExecutorService service = Executors.newFixedThreadPool(noOfThreads)) {
      for (int i = 0; i < noOfThreads; i++) {
        int leftIndex = i * subArrSize; // inclusive
        int rightIndex = Math.min(numbers.length, (i + 1) * subArrSize); // Exclusive

        System.out.println(STR."\{leftIndex}|\{rightIndex}");

        partialResults.add(service.submit(() -> sum(numbers, leftIndex, rightIndex)));
      }
    }

    long sum = partialResults.stream().map(e -> {
      try {
        return e.get(100, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException ex) {
        throw new RuntimeException(ex);
      }
    }).mapToLong(e -> e).sum();

    System.out.println(STR."Sum is \{sum}");
  }

  private static long sum(final int[] arr, final int leftIndex, final int rightIndex) {
    long sum = 0;
    for (int i = leftIndex; i < rightIndex; i++) {
      sum += arr[i];
    }
    return sum;
  }
}

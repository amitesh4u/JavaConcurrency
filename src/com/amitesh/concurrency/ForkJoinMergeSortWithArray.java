package com.amitesh.concurrency;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * Implementing parallel merge sort using Fork Join and array. No of Threads are limited normally equalling the
 * no of cores. In that scenario rest of the sorting will continue in sequential way
 */

public class ForkJoinMergeSortWithArray {

  public static void main(String[] args) {
    int[] arr = {12, 11, 13, 5, 6, 7, 16, 14, 24, 34, 54, 1, 56, 82, 42};
    int noOfThreads = Runtime.getRuntime()
        .availableProcessors(); // No of Threads can be same as no of cores

    MergeSortArrayTask mergeSortTask = new MergeSortArrayTask(arr, noOfThreads);

    System.out.println("Given array is");
    mergeSortTask.printArray();

    mergeSortTask.invoke();

    System.out.println("\nSorted array is");
    mergeSortTask.printArray();
  }
}

class MergeSortArrayTask extends RecursiveAction {

  private final int[] numbers;
  private final int noOfThreads;

  public MergeSortArrayTask(final int[] numbers, final int noOfThreads) {
    this.numbers = numbers;
    this.noOfThreads = noOfThreads;
  }

  @Override
  protected void compute() {
    if (noOfThreads == 1) {
      System.out.println("Out of Threads. Using Sequential sorting");
      sequentialSort(numbers);
    } else {
      System.out.println(STR."Fork Processed by \{Thread.currentThread().threadId()}");
      int middleIndex = numbers.length / 2;

      int[] leftSubArray = Arrays.copyOfRange(numbers, 0, middleIndex);
      int[] rightSubArray = Arrays.copyOfRange(numbers, middleIndex, numbers.length);

      MergeSortArrayTask leftSorter = new MergeSortArrayTask(leftSubArray, noOfThreads/2);
      MergeSortArrayTask rightSorter = new MergeSortArrayTask(rightSubArray, noOfThreads/2);

      invokeAll(leftSorter, rightSorter);

      merge(leftSubArray, rightSubArray, numbers);
    }
  }

  public void sequentialSort(int[] numbers) {
    if (numbers.length <= 1)
      return;

    int mid = numbers.length / 2;

    int[] left = Arrays.copyOfRange(numbers, 0, mid);
    int[] right = Arrays.copyOfRange(numbers, mid, numbers.length);

    sequentialSort(left);
    sequentialSort(right);

    merge(left, right, numbers);
  }

  private void merge(int[] leftSubArray, int[] rightSubArray, int[] originalArray) {
    System.out.println(STR."Join Processed by \{Thread.currentThread().threadId()}");
    int i = 0;
    int j = 0;
    int k = 0;

    while (i < leftSubArray.length && j < rightSubArray.length) {
      if (leftSubArray[i] < rightSubArray[j])
        originalArray[k++] = leftSubArray[i++];
      else
        originalArray[k++] = rightSubArray[j++];
    }

    while (i < leftSubArray.length) {
      originalArray[k++] = leftSubArray[i++];
    }

    while (j < rightSubArray.length) {
      originalArray[k++] = rightSubArray[j++];
    }
  }

  void printArray() {
    for (int num : numbers) {
      System.out.print(STR."\{num} ");
    }
    System.out.println("\n");
  }
}


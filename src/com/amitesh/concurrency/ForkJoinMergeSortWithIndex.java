package com.amitesh.concurrency;

import java.util.concurrent.RecursiveAction;

/**
 * Implementing parallel merge sort using Fork Join and indices. No of Threads are limited normally equalling the
 * no of cores. In that scenario rest of the sorting will continue in sequential way
 */

public class ForkJoinMergeSortWithIndex {

  public static void main(String[] args) {
    int[] arr = {12, 11, 13, 5, 6, 7, 16, 14, 24, 34, 54, 1, 56, 82, 42};
    int noOfThreads = Runtime.getRuntime()
        .availableProcessors(); // No of Threads can be same as no of cores

    MergeSortIndexTask mergeSortTask = new MergeSortIndexTask(arr, 0, arr.length - 1, noOfThreads);

    System.out.println("Given array is");
    mergeSortTask.printArray();

    mergeSortTask.invoke();

    System.out.println("\nSorted array is");
    mergeSortTask.printArray();
  }
}

class MergeSortIndexTask extends RecursiveAction {

  private final int[] numbers;
  private final int left;
  private final int right;
  private final int noOfThreads;

  public MergeSortIndexTask(final int[] numbers, final int left, final int right,
      final int noOfThreads) {
    this.numbers = numbers;
    this.left = left;
    this.right = right;
    this.noOfThreads = noOfThreads;
  }

  @Override
  protected void compute() {
    if (left >= right) {
      return;
    }
    int middle = (left + right) / 2;

    if (noOfThreads == 1) {
      System.out.println("Out of Threads. Using Sequential sorting");
      sequentialSort(left, middle);
      sequentialSort(middle + 1, right);
    } else {
      System.out.println(STR."Fork Processed by \{Thread.currentThread().threadId()}");
      /* Dividing the Thread count in half to precess Left and Right sub list */
      MergeSortIndexTask mergeSortLeftTask = new MergeSortIndexTask(numbers, left, middle,
          noOfThreads / 2);
      MergeSortIndexTask mergeSortRightTask = new MergeSortIndexTask(numbers, middle + 1, right,
          noOfThreads / 2);

      invokeAll(mergeSortLeftTask, mergeSortRightTask);
    }
    merge(left, middle, right);
  }

  public void sequentialSort(int left, int right) {
    if (left >= right) {
      return;
    }
    int middle = (left + right) / 2;

    sequentialSort(left, middle);
    sequentialSort(middle + 1, right);
    merge(left, middle, right);
  }

  private void merge(final int left, final int mid, final int right) {
    System.out.println(STR."Join Processed by \{Thread.currentThread().threadId()}");
    // Find sizes of two sub arrays to be merged
    int leftArrLen = mid - left + 1;
    int rightArrLen = right - mid;

    // Create temp arrays
    int[] leftArr = new int[leftArrLen];
    int[] rightArr = new int[rightArrLen];

    // Copy data to temp arrays
    System.arraycopy(numbers, left, leftArr, 0, leftArrLen);
    System.arraycopy(numbers, mid + 1, rightArr, 0, rightArrLen);

    // Merge the temp arrays
    // Initial indices of first and second sub arrays
    int i = 0, j = 0;

    // Initial index of merged sub array array
    int k = left;
    while (i < leftArrLen && j < rightArrLen) {
      if (leftArr[i] <= rightArr[j]) {
        numbers[k] = leftArr[i];
        i++;
      } else {
        numbers[k] = rightArr[j];
        j++;
      }
      k++;
    }

    // Copy remaining elements of leftArr[] if any
    while (i < leftArrLen) {
      numbers[k] = leftArr[i];
      i++;
      k++;
    }

    // Copy remaining elements of rightArr[] if any
    while (j < rightArrLen) {
      numbers[k] = rightArr[j];
      j++;
      k++;
    }
  }

  void printArray() {
    for (int num : numbers) {
      System.out.print(STR."\{num} ");
    }
    System.out.println("\n");
  }
}


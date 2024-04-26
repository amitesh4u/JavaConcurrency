package com.amitesh.concurrency;

/**
 * Implementing parallel merge sort using Threads. No of Threads are limited normally equalling the
 * no of cores. In that scenario rest of the sorting will continue in sequential way
 */

public class ParallelMergeSort {

  public static void main(String[] args) {
    int[] arr = {12, 11, 13, 5, 6, 7, 16, 14, 24, 34, 54, 1, 56, 82, 42};

    MergeSort mergeSort = new MergeSort(arr);

    System.out.println("Given array is");
    mergeSort.printArray();

    int noOfThreads = Runtime.getRuntime().availableProcessors(); // No of Threads can be same as no of cores
    mergeSort.sort(0, arr.length - 1, noOfThreads);

    System.out.println("\nSorted array is");
    mergeSort.printArray();
  }
}

class MergeSort {

  private final int[] numbers;

  public MergeSort(final int[] numbers) {
    this.numbers = numbers;
  }

  public void sort(final int left, final int right, final int noOfThreads) {
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
      Thread leftWorker = new Thread(() -> sort(left, middle, noOfThreads / 2));
      Thread rightWorker = new Thread(() -> sort(middle + 1, right, noOfThreads / 2));

      leftWorker.start();
      rightWorker.start();

      try {
        leftWorker.join();
        rightWorker.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
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

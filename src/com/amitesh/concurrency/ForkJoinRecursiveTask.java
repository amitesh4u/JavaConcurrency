package com.amitesh.concurrency;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinRecursiveTask {

  public static void main(String[] args) {
    try(ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
      SimpleRecursiveTask simpleRecursiveTask = new SimpleRecursiveTask(400);
      Integer result = forkJoinPool.invoke(simpleRecursiveTask);
      System.out.println(STR."Double of the number 400 is \{result}");
    }
  }

  private static class SimpleRecursiveTask extends RecursiveTask<Integer> {
    private final int simulatedWork;

    public SimpleRecursiveTask(int simulatedWork) {
      this.simulatedWork = simulatedWork;
    }

    @Override
    protected Integer compute() {
      if (simulatedWork > 100) {
        System.out.println(STR."Parallel execution and split the tasks...\{simulatedWork}");

        SimpleRecursiveTask simpleRecursiveAction1 = new SimpleRecursiveTask(simulatedWork / 2);
        SimpleRecursiveTask simpleRecursiveAction2 = new SimpleRecursiveTask(simulatedWork / 2);

        simpleRecursiveAction1.fork();
        simpleRecursiveAction2.fork();

        int solution = 0;
        solution += simpleRecursiveAction1.join();
        solution += simpleRecursiveAction2.join();

        return solution;
      } else {
        System.out.println(
            STR."No need for parallel execution, sequential is OK for this task...\{simulatedWork}");
        return 2 * simulatedWork; // Any action
      }
    }
  }
}

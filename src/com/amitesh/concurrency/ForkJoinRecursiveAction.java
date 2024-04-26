package com.amitesh.concurrency;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinRecursiveAction {

  public static void main(String[] args) {
    try(ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) {
      SimpleRecursiveAction simpleRecursiveAction = new SimpleRecursiveAction(400);
      forkJoinPool.invoke(simpleRecursiveAction);
    }
  }

  private static class SimpleRecursiveAction extends RecursiveAction {
    private final int simulatedWork;

    public SimpleRecursiveAction(int simulatedWork) {
      this.simulatedWork = simulatedWork;
    }

    @Override
    protected void compute() {
      if (simulatedWork > 100) {
        System.out.println(STR."Parallel execution and split the tasks...\{simulatedWork}");

        SimpleRecursiveAction simpleRecursiveAction1 = new SimpleRecursiveAction(simulatedWork / 2);
        SimpleRecursiveAction simpleRecursiveAction2 = new SimpleRecursiveAction(simulatedWork / 2);

        simpleRecursiveAction1.fork();
        simpleRecursiveAction2.fork();
      } else {
        System.out.println(
            STR."No need for parallel execution, sequential is OK for this task...\{simulatedWork}");
      }
    }
  }
}

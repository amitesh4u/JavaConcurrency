package com.amitesh.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class AtomicReferenceDemo {

  private static final int SIMULATION_TIME_SECONDS = 5;

  public static void main(String[] args) throws InterruptedException {
    StandardStack<Integer> standardStack = new StandardStack<>();
    System.out.println(STR."Executing Standard stack for \{SIMULATION_TIME_SECONDS} seconds!!");
    executeStack(standardStack);
    System.out.println(
        STR."\{standardStack.getCounter()} operations were performed in \{SIMULATION_TIME_SECONDS} seconds");

    LockFreeStack<Integer> lockFreeStack = new LockFreeStack<>();
    System.out.println(STR."Executing Lock Free stack for \{SIMULATION_TIME_SECONDS} seconds!!");
    executeStack(lockFreeStack);
    System.out.println(
        STR."\{lockFreeStack.getCounter()} operations were performed in \{SIMULATION_TIME_SECONDS} seconds");

  }

  private static void executeStack(MyStack<Integer> stack) throws InterruptedException {
    Random random = new Random();

    for (int i = 0; i < 100000; i++) {
      stack.push(random.nextInt());
    }

    List<Thread> threads = new ArrayList<>();

    int pushingThreads = 2;
    int poppingThreads = 2;

    for (int i = 0; i < pushingThreads; i++) {
      Thread thread = new Thread(() -> {
        while (true) {
          stack.push(random.nextInt());
        }
      });

      thread.setDaemon(true);
      threads.add(thread);
    }

    for (int i = 0; i < poppingThreads; i++) {
      Thread thread = new Thread(() -> {
        while (true) {
          stack.pop();
        }
      });

      thread.setDaemon(true);
      threads.add(thread);
    }

    for (Thread thread : threads) {
      thread.start();
    }

    TimeUnit.SECONDS.sleep(SIMULATION_TIME_SECONDS);
  }

  public interface MyStack<T> {

    void push(T i);

    void pop();
  }

  public static class LockFreeStack<T> implements MyStack<T> {

    private final AtomicReference<StackNode<T>> head = new AtomicReference<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void push(T value) {
      StackNode<T> newHeadNode = new StackNode<>(value);

      while (true) {
        StackNode<T> currentHeadNode = head.get();
        newHeadNode.next = currentHeadNode;
        if (head.compareAndSet(currentHeadNode, newHeadNode)) {
          break;
        } else {
          LockSupport.parkNanos(1);
        }
      }
      counter.incrementAndGet();
    }


    @Override
    public void pop() {
      StackNode<T> currentHeadNode = head.get();
      StackNode<T> newHeadNode;

      while (currentHeadNode != null) {
        newHeadNode = currentHeadNode.next;
        if (head.compareAndSet(currentHeadNode, newHeadNode)) {
          break;
        } else {
          LockSupport.parkNanos(1);
          currentHeadNode = head.get();
        }
      }
      counter.incrementAndGet();
    }

    public int getCounter() {
      return counter.get();
    }
  }

  public static class StandardStack<T> implements MyStack<T> {

    private StackNode<T> head;
    private int counter = 0;

    @Override
    public synchronized void push(T value) {
      StackNode<T> newHead = new StackNode<>(value);
      newHead.next = head;
      head = newHead;
      counter++;
    }

    @Override
    public synchronized void pop() {
      if (head == null) {
        counter++;
        return;
      }

      T value = head.value;
      head = head.next;
      counter++;
    }

    public int getCounter() {
      return counter;
    }
  }

  private static class StackNode<T> {

    public T value;
    public StackNode<T> next;

    public StackNode(T value) {
      this.value = value;
    }
  }
}

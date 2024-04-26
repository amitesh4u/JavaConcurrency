package com.amitesh.concurrency;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LiveLockDemo {

  private static final boolean ADD_DELAY = Boolean.FALSE; // TRUE will create LiveLock situation
  private static final Lock LOCK_A = new ReentrantLock(true);
  private static final Lock LOCK_B = new ReentrantLock(true);

  public static void main(String[] args) throws InterruptedException {
    LiveLockDemo liveLockDemo = new LiveLockDemo();

    new Thread(liveLockDemo::ThreadA).start();
    new Thread(liveLockDemo::ThreadB).start();

    Thread.sleep(100);

    System.exit(0);
  }

  private void ThreadA() {
    while (true) {
      try {
        if (LOCK_A.tryLock(50, TimeUnit.MILLISECONDS)) {
          System.out.println("Thread A is holding Lock A");
          System.out.println("Thread A is trying to acquire Lock B");

          if (ADD_DELAY) {
            try {
              Thread.sleep(5);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }

          if (LOCK_B.tryLock()) {
            System.out.println("Thread A is holding Lock B");
            LOCK_B.unlock();
            System.out.println("Thread A has released Lock B");
          } else {
            System.out.println("Thread A can not acquire lock B");
            continue;
          }
          break;
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    LOCK_A.unlock();
    System.out.println("Thread A has released Lock A");
  }

  private void ThreadB() {
    while (true) {
      try {
        if (LOCK_B.tryLock(50, TimeUnit.MILLISECONDS)) {
          System.out.println("Thread B is holding Lock B");
          System.out.println("Thread B is trying to acquire Lock A");

          if (ADD_DELAY) {
            try {
              Thread.sleep(5);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }

          if (LOCK_A.tryLock()) {
            System.out.println("Thread B is holding Lock A");
            LOCK_A.unlock();
            System.out.println("Thread B has released Lock A");
          } else {
            System.out.println("Thread B can not acquire lock A");
            continue;
          }
          break;
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    LOCK_B.unlock();
    System.out.println("Thread B has released Lock B");
  }
}

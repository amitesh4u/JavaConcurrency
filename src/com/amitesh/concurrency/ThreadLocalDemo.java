package com.amitesh.concurrency;

import java.util.UUID;

/**
 * The ThreadLocal construct allows us to store data that will be accessible only by a specific
 * thread. Simply put, we can imagine that ThreadLocal stores data inside of a map with the thread
 * as the key. When using ThreadLocal, we need to be very careful because every ThreadLocal instance
 * is associated with a particular thread and this thread is created by us, so we have full control
 * over it.
 */
public class ThreadLocalDemo {

  public static void main(String[] args) throws InterruptedException {

    ThreadLocalWithUserContext firstUser = new ThreadLocalWithUserContext(1);
    ThreadLocalWithUserContext secondUser = new ThreadLocalWithUserContext(2);
    new Thread(firstUser).start();
    new Thread(secondUser).start();

    Thread.sleep(3000);

    firstUser.remove();
    secondUser.remove();
  }

  private static String getUserNameForUserId(Integer userId) {
    return STR."\{userId}:\{UUID.randomUUID().toString()}";
  }

  private record ThreadLocalWithUserContext(Integer userId) implements Runnable {

    private static final ThreadLocal<String> userContext = new ThreadLocal<>();

    @Override
    public void run() {
      String userName = getUserNameForUserId(userId);
      userContext.set(userName);
      System.out.println(STR."Thread context for given userId: \{userId} is \{userContext.get()}");
    }

    public void remove(){
      userContext.remove();
    }
  }
}

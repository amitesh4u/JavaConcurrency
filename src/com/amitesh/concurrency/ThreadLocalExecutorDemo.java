package com.amitesh.concurrency;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * The ThreadLocal construct allows us to store data that will be accessible only by a specific
 * thread. Simply put, we can imagine that ThreadLocal stores data inside of a map with the thread
 * as the key. When using ThreadLocal, we need to be very careful because every ThreadLocal instance
 * is associated with a particular thread and this thread is created by us, so we have full control
 * over it.
 */
public class ThreadLocalExecutorDemo {

  public static void main(String[] args) throws InterruptedException {

    /* RejectedExecutionHandler implementation */
    RejectedExecutionHandlerImpl rejectionHandler = new RejectedExecutionHandlerImpl();

    /* Get the ThreadFactory implementation to use */
    ThreadFactory threadFactory = Executors.defaultThreadFactory();

    MyMonitorThread monitor = null;
    /* Creating the ThreadPoolExecutor */
    try (ThreadPoolExecutor executorPool = new ThreadLocalAwareThreadPool(2, 4, 10,
        TimeUnit.SECONDS, new ArrayBlockingQueue<>(2), threadFactory, rejectionHandler)) {

      /* Start the monitoring thread */
      monitor = new MyMonitorThread(executorPool, 400);
      Thread monitorThread = new Thread(monitor);
      monitorThread.start();

      /* Submit work to the thread pool  */
      for (int i = 0; i < 10; i++) {
        executorPool.execute(new ThreadLocalWithUserContext(i));
      }
    } finally {
      Thread.sleep(500);

      //shut down the monitor thread
      if (null != monitor) {
        monitor.shutdown();
      }
    }
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
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      System.out.println(STR."Thread context for given userId: \{userId} is \{userContext.get()}");
    }

    /* Will be executed using reflection from ThreadPoolExecutor */
    @SuppressWarnings("unused")
    public void remove() {
      if (null != userContext.get()) {
        System.out.println(
            STR."Removing ThreadLocal for \{userContext.get()} by Thread \{Thread.currentThread()}");
        userContext.remove();
      }
    }
  }

  private static class ThreadLocalAwareThreadPool extends ThreadPoolExecutor {

    public static final String THREAD_LOCAL_REMOVE_METHOD_NAME = "remove";

    public ThreadLocalAwareThreadPool(int corePoolSize,
        int maximumPoolSize,
        long keepAliveTime,
        TimeUnit unit,
        BlockingQueue<Runnable> workQueue,
        ThreadFactory threadFactory,
        RejectedExecutionHandlerImpl rejectionHandler) {
      super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
          rejectionHandler);
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
      System.out.println(STR."Before Execute thread \{t} and runnable \{r}");
      removeThreadLocal(r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
      System.out.println(STR."After Execute runnable \{r} and throwable \{t}");
      removeThreadLocal(r);
    }

    private static void removeThreadLocal(Runnable r) {
      try {
        r.getClass().getMethod(THREAD_LOCAL_REMOVE_METHOD_NAME).invoke(r);
      } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

  }

  private static class RejectedExecutionHandlerImpl implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
      System.out.println(STR."\{r.toString()} is rejected");
    }
  }

  private static class MyMonitorThread implements Runnable {

    private final ThreadPoolExecutor executor;
    private final int delay;
    private boolean run = true;

    public MyMonitorThread(ThreadPoolExecutor executor, int delay) {
      this.executor = executor;
      this.delay = delay;
    }

    public void shutdown() {
      this.run = false;
    }

    @Override
    public void run() {
      while (run) {
        System.out.printf(
            "[monitor pool/corePool/maxPool]: [%d/%d/%d], Active: %d, Completed: %d, Task: %d, isShutdown: %s, isTerminated: %s%n",
            this.executor.getPoolSize(),
            this.executor.getCorePoolSize(),
            this.executor.getMaximumPoolSize(),
            this.executor.getActiveCount(),
            this.executor.getCompletedTaskCount(),
            this.executor.getTaskCount(),
            this.executor.isShutdown(),
            this.executor.isTerminated());
        try {
          TimeUnit.MILLISECONDS.sleep(delay);
        } catch (InterruptedException e) {
          throw new RuntimeException();
        }
      }
    }
  }
}

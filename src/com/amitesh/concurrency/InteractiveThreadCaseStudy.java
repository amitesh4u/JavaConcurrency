package com.amitesh.concurrency;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** There are hackers trying to guess a password. Police will arrive after a fixed time.
 * If any of the Hacker guesses the password correctly they win but if the Police arrives before that they loose
 */
public class InteractiveThreadCaseStudy {

  private static final int MAX_PASSWORD_LIMIT = 9999;

  public static void main(String[] args) {
    int password = new Random().nextInt(MAX_PASSWORD_LIMIT);
    System.out.println(STR."My Locker password is \{password}");
    Locker locker = new Locker(password);

    List<Thread> tasks = Arrays.asList(new AscendingHackerThread(locker), new DescendingHackerThread(locker), new PoliceThread());
    tasks.forEach(Thread::start);
  }

  private record Locker(int password) {

    public boolean isCorrectPassword(int guessPassword) {
      try {
        Thread.sleep(5);
      } catch (InterruptedException e) {
        //ignore
      }
      return password == guessPassword;
    }
  }

  public static abstract class HackerThread extends Thread {
    private final Locker locker;

    HackerThread(Locker locker){
      this.locker = locker;
      this.setName(this.getClass().getSimpleName());
      this.setPriority(Thread.MAX_PRIORITY);
    }

    @Override
    public void start(){
      System.out.println(STR."Starting Thread \{this.getName()}");
      super.start();
    }

    Locker getLocker(){
      return this.locker;
    }
  }

  public static class AscendingHackerThread extends  HackerThread{

    AscendingHackerThread(Locker locker) {
      super(locker);
    }

    @Override
    public void run(){
      for(int guess = 1 ; guess <= MAX_PASSWORD_LIMIT ; guess++){
        if(getLocker().isCorrectPassword(guess)){
          System.out.println(STR."\{this.getName()} guessed the password as \{guess}");
          System.exit(0);
        }
      }
    }
  }

  public static class DescendingHackerThread extends  HackerThread{

    DescendingHackerThread(Locker locker) {
      super(locker);
    }

    @Override
    public void run(){
      for(int guess = MAX_PASSWORD_LIMIT ; guess > 0 ; guess--){
        if(getLocker().isCorrectPassword(guess)){
          System.out.println(STR."\{this.getName()} guessed the password as \{guess}");
          System.exit(0);
        }
      }
    }
  }

  public static class PoliceThread extends Thread{

    @Override
    public void run(){
      for(int time = 10 ; time > 0 ; time--) {
        System.out.println(STR."Police arriving in \{time} sec!!");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          //ignore
        }
      }

      System.out.println("Police has arrived. Hackers you loose!!");
      System.exit(0);
    }
  }
}

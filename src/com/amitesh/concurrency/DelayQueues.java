package com.amitesh.concurrency;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * This is an unbounded BlockingQueue of objects that implement the Delayed
 * interface
 * <p>
 * - DelayQueue keeps the elements internally until a certain delay has expired
 * <p>
 * - an object can only be taken from the queue when its delay has expired !!! -
 * <p>
 * We cannot place null items in the queue - The queue is sorted so that the
 * object at the head has a delay that has expired for the longest time.
 * <p>
 * If no delay has expired, then there is no head element and poll( ) will
 * return null
 * <p>
 * size() return the count of both expired and unexpired items !!!
 *
 */

public class DelayQueues {

  /**
   * Expected output with no code changes:
   * Starting process at Tue Apr 23 17:41:07 IST 2024
   * Fetching value This is message #1 at Tue Apr 23 17:41:08 IST 2024
   * Fetching value This is message #3 at Tue Apr 23 17:41:11 IST 2024
   * Fetching value This is message #2 at Tue Apr 23 17:41:17 IST 2024
   * Finished process at Tue Apr 23 17:41:17 IST 2024
   * @param args arguments
   */
	public static void main(String[] args) {

		System.out.println(STR."Starting process at \{new Date()}");

		BlockingQueue<DelayedWorker> delayQueue = new DelayQueue<>();

		try {
			delayQueue.put(new DelayedWorker(1000, "This is message #1"));
			delayQueue.put(new DelayedWorker(10000, "This is message #2"));
			delayQueue.put(new DelayedWorker(4000, "This is message #3"));
		} catch (InterruptedException e) {
			throw new RuntimeException();
		}

		while (!delayQueue.isEmpty()) {
			try {
				System.out.println(STR."Fetching value \{delayQueue.take()} at \{new Date()}");
			} catch (InterruptedException e) {
				throw new RuntimeException();
			}
		}
		System.out.println(STR."Finished process at \{new Date()}");
	}
}

class DelayedWorker implements Delayed {

	private final long duration;
	private final String message;

	public DelayedWorker(long duration, String message) {
		this.duration = System.currentTimeMillis() + duration;
		this.message = message;
	}

	/* To sort the Items by delay time in ascending order */
	@Override
	public int compareTo(Delayed otherDelayed) {
    return Long.compare(this.duration, ((DelayedWorker) otherDelayed).getDuration());
  }

	@Override
	public long getDelay(TimeUnit timeUnit) {
		return timeUnit.convert(duration - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	public long getDuration() {
		return duration;
	}

	@Override
	public String toString() {
		return this.message;
	}
}

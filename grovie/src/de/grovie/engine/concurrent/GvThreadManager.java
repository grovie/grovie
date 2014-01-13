package de.grovie.engine.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * This is the base class that executes thread-level parallel tasks.
 * It contains a pool of a fixed number of worker threads (using Java's ExecutorService)
 * 
 * @author yong
 *
 */
public abstract class GvThreadManager {

	protected ScheduledExecutorService lThreadPool; //java's thread pool implementation
		
	/**
	 * Constructor and thread pool initiation
	 * @param numThreads
	 */
	public GvThreadManager(int numThreads)
	{
		lThreadPool = Executors.newScheduledThreadPool(numThreads);
	}
	
	/**
	 * Shuts down this thread manager
	 */
	public void shutdownAndAwaitTermination() {
	
		// Disable new tasks from being submitted
		lThreadPool.shutdown();
		
		try {
			// Wait a while for existing tasks to terminate
			if (!lThreadPool.awaitTermination(2, TimeUnit.SECONDS)) {
				 // Cancel currently executing tasks
				lThreadPool.shutdownNow();
				// Wait a while for tasks to respond to being cancelled
				if (!lThreadPool.awaitTermination(2, TimeUnit.SECONDS))
					System.err.println("GvService did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			lThreadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * Starts this thread manager
	 */
	public abstract void start();
}

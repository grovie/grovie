package de.grovie.engine.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This is the base class that executes thread-level parallel tasks.
 * It contains a pool of a fixed number of worker threads (using Java's ExecutorService)
 * The class is intended to maximize CPU throughput for preparing renderer resources 
 * (such as textures, VBO, etc.) so that the rendering thread/GPU become aware of scene
 * updates in a shorter time. 
 * 
 * @author yong
 *
 */
public class GvThreadManager {

	private ExecutorService lThreadPool; //java's thread pool implementation
	
	GvMessageQueue qLoad = new GvMessageQueue();
	GvMessageQueue qRequest = new GvMessageQueue();
	GvMessageQueue qDone = new GvMessageQueue();
	
	/**
	 * Constructor and thread pool initiation
	 * @param numThreads
	 */
	public GvThreadManager(int numThreads)
	{
		lThreadPool = Executors.newFixedThreadPool(numThreads);
	}
	
	/**
	 * Starts this thread manager
	 */
	public void start()
	{
		
	}
	
	/**
	 * Shuts down this thread manager
	 */
	public void shutdownAndAwaitTermination() {
		// Disable new tasks from being submitted
		lThreadPool.shutdown();
		
		try {
			// Wait a while for existing tasks to terminate
			if (!lThreadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				 // Cancel currently executing tasks
				lThreadPool.shutdownNow();
				// Wait a while for tasks to respond to being cancelled
				if (!lThreadPool.awaitTermination(60, TimeUnit.SECONDS))
					System.err.println("GvService did not terminate");
			}
		} catch (InterruptedException ie) {
			// (Re-)Cancel if current thread also interrupted
			lThreadPool.shutdownNow();
			// Preserve interrupt status
			Thread.currentThread().interrupt();
		}
	}
}

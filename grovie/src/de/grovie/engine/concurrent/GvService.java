package de.grovie.engine.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class provides a service to execute thread-level parallel tasks.
 * It contains a pool of a fixed number of worker threads (using Java's ExecutorService)
 * The class is intended to maximize CPU throughput for preparing renderer resources 
 * (such as textures, VBO, etc.) so that the rendering thread/GPU become aware of scene
 * updates in a shorter time. 
 * @author yong
 *
 */
public class GvService{

	private ExecutorService lThreadPool; //java's thread pool implementation
	
	public GvService(int numThreads)
	{
		lThreadPool = Executors.newFixedThreadPool(numThreads);

	}
	
	/**
	 * Post a message/job to this service for execution as soon as a worker thread is available
	 * @param message
	 */
	public synchronized void post(GvMessage message)
	{
		lThreadPool.execute(message);
	}

	/**
	 * Shuts down this service
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

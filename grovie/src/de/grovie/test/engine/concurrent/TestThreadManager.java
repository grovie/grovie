package de.grovie.test.engine.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.grovie.engine.concurrent.GvMessageQueue;

public class TestThreadManager {

	
	public static void main(String[] args) throws InterruptedException {
		
		GvMessageQueue qLoad = new GvMessageQueue();
		GvMessageQueue qRequest = new GvMessageQueue();
		GvMessageQueue qDone = new GvMessageQueue();
		
		ExecutorService lThreadPool = Executors.newFixedThreadPool(3);
		
		//start load thread service
		lThreadPool.execute(new TestThreadLoad(qLoad,qRequest, qDone));
		
		//start worker thread service
		lThreadPool.execute(new TestThreadWorker(qLoad,qRequest, qDone));
		
		//start done thread service
		lThreadPool.execute(new TestThreadDone(qLoad,qRequest, qDone));
		
		//send request messages
		for(int i=0; i<100; i++)
		{
			qLoad.offer(new TestMessageRequest((double)i));
		}
		
		
	}
}

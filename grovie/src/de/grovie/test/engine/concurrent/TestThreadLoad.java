package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMessage;
import de.grovie.engine.concurrent.GvMessageQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadLoad extends GvThread {

	GvMessageQueue lQueueLoad;
	GvMessageQueue lQueueRequest;
	GvMessageQueue lQueueDone;
	
	public TestThreadLoad() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadLoad(
			GvMessageQueue queueLoad,
			GvMessageQueue queueRequest, 
			GvMessageQueue queueDone) {
		super();
		lQueueLoad = queueLoad;
		lQueueRequest = queueRequest;
		lQueueDone = queueDone;
	}

	@Override
	public void runThread() throws InterruptedException {
		//int loadCount = 0;
		for(;;)
		{
			GvMessage msg = lQueueLoad.poll();
			if(msg==null)
				continue;
			else
			{
				//loadCount++;
				//System.out.println("load count:" + loadCount);
				//System.out.println("Load taken");
				
				//theoretically, I/O is performed here to get data from secondary storage
				
				//request work to be done at worker threads
				lQueueRequest.offer(msg);
			}
		}
	}
	
	
}

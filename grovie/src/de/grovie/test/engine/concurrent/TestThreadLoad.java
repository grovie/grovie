package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadLoad extends GvThread {

	GvMsgQueue lQueueLoad;
	GvMsgQueue lQueueRequest;
	GvMsgQueue lQueueDone;
	
	public TestThreadLoad() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadLoad(
			GvMsgQueue queueLoad,
			GvMsgQueue queueRequest, 
			GvMsgQueue queueDone) {
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
			GvMsg msg = lQueueLoad.poll();
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

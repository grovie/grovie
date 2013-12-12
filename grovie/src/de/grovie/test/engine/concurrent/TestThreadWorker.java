package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMessage;
import de.grovie.engine.concurrent.GvMessageQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadWorker  extends GvThread{

	GvMessageQueue lQueueLoad;
	GvMessageQueue lQueueRequest;
	GvMessageQueue lQueueDone;
	
	public TestThreadWorker( ) {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadWorker(
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
		for(;;)
		{
			GvMessage msg = lQueueRequest.poll();
			if(msg==null)
				continue;
			else
			{	
				TestMessageRequest msgRequest = (TestMessageRequest)msg;
				double result = msgRequest.lNum * msgRequest.lNum; 
				
				lQueueDone.offer(new TestMessageDone("Request num " + msgRequest.lNum + " squared is " + result));
			}
		}
	}

}

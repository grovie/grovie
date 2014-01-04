package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadWorker  extends GvThread{

	GvMsgQueue lQueueLoad;
	GvMsgQueue lQueueRequest;
	GvMsgQueue lQueueDone;
	
	public TestThreadWorker( ) {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadWorker(
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
		for(;;)
		{
			GvMsg msg = lQueueRequest.poll();
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

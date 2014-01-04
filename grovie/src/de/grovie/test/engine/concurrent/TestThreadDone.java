package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadDone  extends GvThread{

	GvMsgQueue lQueueLoad;
	GvMsgQueue lQueueRequest;
	GvMsgQueue lQueueDone;

	public TestThreadDone() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadDone(
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
			GvMsg msg = lQueueDone.poll();
			//System.out.println(msg);
			if(msg==null)
				continue;
			else
			{
				//System.out.println("Request taken");

				TestMessageDone msgRequest = (TestMessageDone)msg;
				System.out.println("Done thread: " + msgRequest.lMsg);
			}

		}
	}

}

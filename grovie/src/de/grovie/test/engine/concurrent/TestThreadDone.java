package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMessage;
import de.grovie.engine.concurrent.GvMessageQueue;
import de.grovie.engine.concurrent.GvThread;

public class TestThreadDone  extends GvThread{

	GvMessageQueue lQueueLoad;
	GvMessageQueue lQueueRequest;
	GvMessageQueue lQueueDone;

	public TestThreadDone() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TestThreadDone(
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
			GvMessage msg = lQueueDone.poll();
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

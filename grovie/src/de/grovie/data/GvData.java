package de.grovie.data;

import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThread;
import de.grovie.exception.GvExEngineConcurrentThreadInitFail;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class performs CPU tasks that prepare data for visualization.
 * It communicates with the database (on another thread) and the
 * renderer (on another thread) via message queues.
 * 
 * @author yong
 *
 */
public class GvData extends GvThread {

	private Object lContextRenderer;
	
	//window system
	private GvWindowSystem lWindowSystem;
		
	//message queues - for communication with other threads
	GvMsgQueue<GvData> lQueueIn;
	GvMsgQueue<GvRenderer> lQueueOutRenderer;
	GvMsgQueue<GvDb> lQueueDb;
	
	public GvData() {
	}

	public GvData(GvWindowSystem windowSystem,
			GvMsgQueue<GvData> queueData,
			GvMsgQueue<GvRenderer> queueRenderer,
			GvMsgQueue<GvDb> queueDb) {
		
		lWindowSystem = windowSystem;
		
		lQueueIn = queueData;
		lQueueOutRenderer = queueRenderer;
		lQueueDb = queueDb;
	}

	@Override
	public void runThread() throws InterruptedException, GvExEngineConcurrentThreadInitFail 
	{	
//		int count = 0;
		
		//thread loop
		for(;;)
		{
			//check for msgs from incoming queue
			GvMsg<GvData> msg = lQueueIn.poll();
			
			//if no messages, continue check
			if(msg==null)
			{
//				System.out.println(count);
//				count++;
				continue;
			}
			else
			{	
				msg.process(this);
			}
		}
	}
	
	public void setupContext(Object contextRenderer)
	{
		lContextRenderer = contextRenderer;
		lWindowSystem.getInstance(lContextRenderer);
	}
}

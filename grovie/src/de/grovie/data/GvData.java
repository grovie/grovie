package de.grovie.data;
import de.grovie.db.GvDb;
import de.grovie.db.GvDbInteger;
import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvMsgRenderSwap;
import de.grovie.engine.concurrent.GvThread;
import de.grovie.exception.GvExEngineConcurrentThreadInitFail;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDrawGroup;
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
public abstract class GvData extends GvThread {
	
	//window system
	protected GvWindowSystem lWindowSystem;
		
	//message queues - for communication with other threads
	protected GvMsgQueue<GvData> lQueueIn;
	protected GvMsgQueue<GvRenderer> lQueueOutRenderer;
	protected GvMsgQueue<GvDb > lQueueDb;
	
	//messages (reusable)
	private GvMsgRenderSwap lMsgRenderSwap;
	
	//draw group to be updated
	protected GvDrawGroup lDrawGroup;
	
	//latest camera information
	protected GvCamera lCamera;
	
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
		
		//draw group is null until it arrives in message queue from renderer thread 
		lDrawGroup = null;
		
		lMsgRenderSwap = new GvMsgRenderSwap();
	}

	@Override
	public void runThread() throws InterruptedException, GvExEngineConcurrentThreadInitFail 
	{	
		//thread loop
		for(;;)
		{
			//check for msgs from incoming queue
			GvMsg<GvData> msg = lQueueIn.poll();
			
			//if no messages, continue check
			if(msg==null)
			{
				continue;
			}
			else
			{	
				msg.process(this);
			}
		}
	}
	
	//standard out-going messages
	public void sendBufferSwap() {
		lQueueOutRenderer.offer(lMsgRenderSwap);
	}
	
	//incoming msg handlers
	public abstract void receiveBufferSet(GvDrawGroup drawGroup);
	public abstract void receiveCameraUpdate(GvCamera camera);
	public abstract void receiveSceneUpdate(GvDbInteger integer);
}

package de.grovie.data;
import com.tinkerpop.blueprints.Graph;

import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvMsgRenderSwap;
import de.grovie.engine.concurrent.GvThread;
import de.grovie.exception.GvExEngineConcurrentThreadInitFail;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvRenderer;

/**
 * This class performs CPU tasks that prepare data for visualization.
 * It communicates with the database (on another thread) and the
 * renderer (on another thread) via message queues.
 * 
 * @author yong
 *
 */
public class GvData extends GvThread {

	//message queues - for communication with other threads
	private GvMsgQueue<GvData> lQueueIn;
	private GvMsgQueue<GvRenderer> lQueueOutRenderer;

	//messages (reusable)
	private GvMsgRenderSwap lMsgRenderSwap;

	//draw group to be updated
	private GvDrawGroup lDrawGroup;

	//latest camera information
	private GvCamera lCamera;

	//latest simulation step and graph database reference
	private int lStepId;
	private Graph lGraph;

	//latest set of processed data sent to renderer
	private int lLatestStepId;
	private GvCamera lLatestCamera;
	

	public GvData() {
	}

	public GvData(
			GvMsgQueue<GvData> queueData,
			GvMsgQueue<GvRenderer> queueRenderer,
			GvMsgQueue<GvDb> queueDb) {

		lQueueIn = queueData;
		lQueueOutRenderer = queueRenderer;

		//reusable msg object
		lMsgRenderSwap = new GvMsgRenderSwap();

		//null until arrival in message queue from renderer thread 
		lDrawGroup = null;
		lCamera = null;

		//null until arrival in message queue from db thread
		lStepId = -1;
		lGraph = null;

		//tracking step id of latest  set of geometry sent to renderer 
		lLatestStepId = -1;
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
	public void receiveCameraUpdate(GvCamera camera) {
		lCamera = camera;
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry(); //TODO: need to use time buffer at msg queue handling to prevent flooding
	}

	public void receiveSceneUpdate(int stepId, Graph graph)
	{
		lStepId = stepId;
		lGraph = graph;
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry();
	}

	public void receiveBufferSet(GvDrawGroup drawGroup){
		lDrawGroup = drawGroup;
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry();
	}

	/**
	 * Inserts geometry into latest(if existing) update buffer received from renderer.
	 * 
	 * @return true if geometry is inserted successfully, false otherwise.
	 */
	private void sendGeometry() 
	{
		//check if any of the required items are missing for geometry insertion
		if((lDrawGroup==null)||(lCamera==null)||(lGraph==null)||(lStepId==-1))
			return;

		//check if any data has changed, or if camera has changed
		//if nothing has changed, no updated geometry needs to be sent to renderer
		if((lStepId == lLatestStepId)&&(lCamera.compare(lLatestCamera)))
			return;
		
		try{
			//TODO: acceleration structure updates, geometry generation and insertion
			
			//if geometry was inserted and sent to renderer,
			//set reference to draw-group null, wait for new reference from rendering thread
			lDrawGroup = null; 
			lLatestStepId = lStepId;
			lCamera.copyCamera(lLatestCamera);
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}

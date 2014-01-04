package de.grovie.renderer;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThread;
import de.grovie.exception.GvExEngineConcurrentThreadInitFail;
import de.grovie.renderer.renderstate.GvRenderState;
import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class is an abstract renderer above any graphics API.
 * An instance of this class runs as an independent (rendering) 
 * thread from the other components of the GroViE engine.
 * 
 * @author yong
 *
 */
public abstract class GvRenderer extends GvThread {
	
	//message queues - for communication with other threads
	protected GvMsgQueue<GvRenderer > lQueueIn;
	protected GvMsgQueue<GvData> lQueueOutData;
	
	//window system
	private GvWindowSystem lWindowSystem;
	private String lWindowTitle;
	
	//variables related to drawing 
	protected GvDevice lDevice;					//rendering device factory - objects can be shared between contexts
	protected GvContext lContext;				//rendering context factory
	private GvIllustrator lIllustrator;			//drawing mechanism and 3d pipelines
	private GvGraphicsWindow lGraphicsWindow;	//drawing canvas
	private GvAnimator lAnimator;				//looping mechanism for this rendering thread
	
	//state machine that controls interaction with state of renderer
	private GvRendererStateMachine lRendererStateMachine;
	
	//current settings of graphics API
	protected GvRenderState lRenderState;	
	
	public GvRenderer(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight,
			GvMsgQueue<GvRenderer > queueIn,
			GvMsgQueue<GvData> queueOutData)
	{	
		lWindowSystem = windowSystem;
		lWindowTitle = windowTitle;
		
		lRendererStateMachine = new GvRendererStateMachine(windowWidth,windowHeight);
		
		lQueueIn = queueIn;
		lQueueOutData = queueOutData;
	}
	
	@Override
	public void runThread() throws GvExEngineConcurrentThreadInitFail {
		
		//check if message queues have been set
		if((lQueueIn == null) || (lQueueOutData==null))
		{
			throw new GvExEngineConcurrentThreadInitFail("Message queues for renderer absent.");
		}
		
		lDevice = createDevice();
		lContext = createContext();
		lIllustrator = createIllustrator();
		lAnimator = createAnimator();
		lGraphicsWindow = lDevice.createWindow(
				lWindowSystem,
				this);
	}
	
	public GvRendererStateMachine getRendererStateMachine()
	{
		return lRendererStateMachine;
	}
	
	public GvIllustrator getIllustrator()
	{
		return lIllustrator;
	}
	
	public String getWindowTitle()
	{
		return lWindowTitle;
	}
	
	/**
	 * Repaints or refreshes the drawing canvas
	 */
	public void refresh()
	{
		lGraphicsWindow.getWindowSystem().getCanvas().refresh();
	}
	
	public GvDevice getDevice()
	{
		return lDevice;
	}
	
	public GvAnimator getAnimator()
	{
		return lAnimator;
	}
	
	public GvContext getContext()
	{
		return lContext;
	}
	
	public void processMessages() 
	{
		int msgCount = lQueueIn.size();
		
		for(int i=0; i<msgCount; ++i)
		{
			GvMsg<GvRenderer > msg = lQueueIn.poll();
			msg.process(this);
		}
	}
	
	public abstract GvDevice createDevice();
	public abstract GvContext createContext();
	public abstract GvIllustrator createIllustrator();
	public abstract GvAnimator createAnimator();
	
	public abstract void updateRenderState(GvRenderState newState, Object context);

	public abstract void sendUpdateBuffer();
	
	public abstract void swapBuffers();
}

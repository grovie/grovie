package de.grovie.data;

import javax.media.opengl.GLContext;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvMsgRenderBegin;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GL2.GvBufferSetGL2;
import de.grovie.renderer.windowsystem.GvWindowSystem;

public class GvDataGL2 extends GvData {

	protected GLContext lContext;
	private GvBufferSet lBufferSet;
	
	public GvDataGL2(GvWindowSystem windowSystem,
			GvMsgQueue<GvData> lQueueData,
			GvMsgQueue<GvRenderer> lQueueRenderer, GvMsgQueue<GvDb> lQueueDb) 
	{
		super(windowSystem, lQueueData, lQueueRenderer, lQueueDb);
	}

	@Override
	public void setupContext(Object contextRenderer) 
	{
		setupContext((GLContext)contextRenderer);
	}
	
	private void setupContext(GLContext contextRenderer)
	{
		lWindowSystem.getInstanceInvisible(contextRenderer);
		
		//create new context on this thread that shares VBO/IBO with 
		//renderer thread's context
		GLCanvas canvas = (GLCanvas)lWindowSystem.getCanvas();
		lContext = canvas.createContext(contextRenderer);
		lContext.makeCurrent();
	}

	@Override
	public void sendRenderBegin() {
		lQueueOutRenderer.offer(new GvMsgRenderBegin());
	}

	@Override
	public void receiveBufferSet(GvBufferSet bufferSet) {
		lBufferSet = bufferSet;
	}
}

package de.grovie.engine.concurrent;

import de.grovie.renderer.GvRenderer;

public class GvMsgRenderSwap implements GvMsgRender {

	@Override
	public void process(GvRenderer target) {
		
		target.swapBuffers();
	
		target.sendUpdateBuffer();
	}

}

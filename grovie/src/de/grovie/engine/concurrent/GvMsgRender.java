package de.grovie.engine.concurrent;

import de.grovie.renderer.GvRenderer;

public interface GvMsgRender extends GvMsg<GvRenderer> {

	@Override
	public void process(GvRenderer target);
	
}

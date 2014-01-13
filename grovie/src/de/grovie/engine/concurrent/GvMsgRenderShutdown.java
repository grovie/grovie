package de.grovie.engine.concurrent;

import de.grovie.renderer.GvRenderer;

public class GvMsgRenderShutdown implements GvMsgRender {

	@Override
	public void process(GvRenderer target) {
		target.shutdown();
	}
}

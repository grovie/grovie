package de.grovie.engine.renderer.AWTGL;

import de.grovie.engine.GvEngine;
import de.grovie.engine.renderer.GvDevice;
import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvRenderer;

public class GvRendererAWTGL extends GvRenderer{

	public GvRendererAWTGL(GvEngine engine) {
		super(engine);
	}

	@Override
	public GvDevice createDevice() {
		return new GvDeviceAWTGL();
	}

	@Override
	public GvEventListener createEventListener() {
		return new GvEventListenerAWTGL();
	}

}

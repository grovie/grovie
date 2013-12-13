package de.grovie.engine.renderer.GL3;

import de.grovie.engine.GvEngine;
import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvRendererGL3 extends GvRenderer{

	public GvRendererGL3(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight) {
		super(windowSystem,windowTitle,windowWidth,windowHeight);
	}

	@Override
	public GvDevice createDevice() {
		return new GvDeviceGL3();
	}

	@Override
	public GvEventListener createEventListener() {
		return new GvEventListenerGL3();
	}

}

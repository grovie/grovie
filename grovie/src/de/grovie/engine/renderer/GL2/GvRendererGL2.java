package de.grovie.engine.renderer.GL2;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvRendererGL2 extends GvRenderer{

	public GvRendererGL2(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight) {
		super(windowSystem,windowTitle,windowWidth,windowHeight);
	}

	@Override
	public GvDevice createDevice() {
		return new GvDeviceGL2();
	}

	@Override
	public GvEventListener createEventListener() {
		return new GvEventListenerGL2(this);
	}
}

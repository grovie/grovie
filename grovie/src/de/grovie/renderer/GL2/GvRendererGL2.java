package de.grovie.renderer.GL2;

import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

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
		return new GvDeviceGL2(this);
	}

	@Override
	public GvIllustrator createIllustrator() {
		return new GvIllustratorGL2(this);
	}
}

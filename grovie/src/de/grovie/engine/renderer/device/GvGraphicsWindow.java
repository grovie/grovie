package de.grovie.engine.renderer.device;

import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

/**
 * This class is a container for a rendering context.
 * 
 * @author yong
 *
 */
public class GvGraphicsWindow {

	private GvWindowSystem lWindowSystem;
	
	public GvGraphicsWindow(GvWindowSystem windowSystem)
	{
		lWindowSystem = windowSystem;
	}
	
	public GvWindowSystem getWindowSystem()
	{
		return lWindowSystem;
	}
}

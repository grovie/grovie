package de.grovie.renderer;

import de.grovie.renderer.windowsystem.GvWindowSystem;

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
	
	public void close()
	{
		lWindowSystem.close();
	}
}

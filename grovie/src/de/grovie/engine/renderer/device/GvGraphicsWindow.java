package de.grovie.engine.renderer.device;

import javax.media.opengl.GLEventListener;

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
}

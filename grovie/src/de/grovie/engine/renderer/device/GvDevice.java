package de.grovie.engine.renderer.device;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;


public abstract class GvDevice {

	public abstract GvGraphicsWindow createWindow(int width, 
			int height, 
			GvEventListener eventListener,
			GvWindowSystem windowSystem,
			String windowTitle);
	
	public abstract GvShaderProgram createShaderProgram(String vertexShaderSource, 
			String geometryShaderSource,
			String fragmentShaderSource);
	
	public abstract GvVertexBuffer createVertexBuffer(int sizeInBytes);

}

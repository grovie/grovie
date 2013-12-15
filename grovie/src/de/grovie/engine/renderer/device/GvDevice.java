package de.grovie.engine.renderer.device;

import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;


public abstract class GvDevice {

	public abstract GvGraphicsWindow createWindow(
			GvWindowSystem windowSystem,
			GvRenderer renderer);
	
	public abstract GvShaderProgram createShaderProgram(String vertexShaderSource, 
			String geometryShaderSource,
			String fragmentShaderSource);
	
	public abstract GvVertexBuffer createVertexBuffer(int sizeInBytes);

}

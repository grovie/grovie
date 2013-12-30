package de.grovie.renderer;

import de.grovie.exception.GvExceptionRendererShaderProgram;
import de.grovie.exception.GvExceptionRendererVertexBuffer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class represents a factory for graphics-related entities
 * that can be shared between contexts.
 * 
 * @author yong
 *
 */
public abstract class GvDevice {

	protected GvRenderer lRenderer;
	
	public GvDevice(GvRenderer renderer)
	{
		lRenderer = renderer;
	}
	
	public abstract GvGraphicsWindow createWindow(
			GvWindowSystem windowSystem,
			GvRenderer renderer);
	
	public abstract GvShaderProgram createShaderProgram(String vertexShaderSource, 
			String fragmentShaderSource) throws GvExceptionRendererShaderProgram;
	
	public abstract GvVertexBuffer createVertexBuffer(long sizeInBytes) throws GvExceptionRendererVertexBuffer;

}

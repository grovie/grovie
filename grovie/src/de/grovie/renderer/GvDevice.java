package de.grovie.renderer;

import java.io.InputStream;

import de.grovie.exception.GvExceptionRendererIndexBuffer;
import de.grovie.exception.GvExceptionRendererShaderProgram;
import de.grovie.exception.GvExceptionRendererTexture2D;
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
	
	public abstract GvVertexBuffer createVertexBuffer(long sizeInBytes) 
			throws GvExceptionRendererVertexBuffer;
	
	public abstract GvIndexBuffer createIndexBuffer(long sizeInBytes) 
			throws GvExceptionRendererIndexBuffer;
	
	public abstract GvTexture2D createTexture2D(InputStream inputStream, String dataType) 
			throws GvExceptionRendererTexture2D;
}

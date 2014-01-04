package de.grovie.renderer;

import java.io.InputStream;

import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererShaderProgram;
import de.grovie.exception.GvExRendererTexture2D;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class represents a factory for graphics-related entities
 * that can be shared between contexts.
 * 
 * @author yong
 *
 */
public abstract class GvDevice {
	
	public GvDevice()
	{
	}
	
	public abstract GvGraphicsWindow createWindow(
			GvWindowSystem windowSystem,
			GvRenderer renderer);
	
	public abstract GvShaderProgram createShaderProgram(String vertexShaderSource, 
			String fragmentShaderSource,
			Object libraryAPI) throws GvExRendererShaderProgram;
	
	public abstract GvVertexBuffer createVertexBuffer(long sizeInBytes,
			Object libraryAPI) 
			throws GvExRendererVertexBuffer;
	
	public abstract GvIndexBuffer createIndexBuffer(long sizeInBytes,
			Object libraryAPI) 
			throws GvExRendererIndexBuffer;
	
	public abstract GvTexture2D createTexture2D(InputStream inputStream, String dataType,
			Object libraryAPI) 
			throws GvExRendererTexture2D;
}

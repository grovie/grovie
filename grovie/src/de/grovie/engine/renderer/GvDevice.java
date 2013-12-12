package de.grovie.engine.renderer;


public abstract class GvDevice {

	public abstract GvGraphicsWindow createWindow(int width, int height, GvEventListener lEventListener);
	
	public abstract GvShaderProgram createShaderProgram(String vertexShaderSource, 
			String geometryShaderSource,
			String fragmentShaderSource);
	
	public abstract GvVertexBuffer createVertexBuffer(int sizeInBytes);

}

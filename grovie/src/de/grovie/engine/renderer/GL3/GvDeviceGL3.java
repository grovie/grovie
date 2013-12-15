package de.grovie.engine.renderer.GL3;

import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.device.GvGraphicsWindow;
import de.grovie.engine.renderer.device.GvShaderProgram;
import de.grovie.engine.renderer.device.GvVertexBuffer;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvDeviceGL3 extends GvDevice{

	@Override
	public GvGraphicsWindow  createWindow(
			GvWindowSystem windowSystem,
			GvRenderer renderer) {
		
		GvWindowSystem winSys = windowSystem.getInstance(renderer);
		
		return new GvGraphicsWindowGL3(winSys);
	}

	@Override
	public GvShaderProgram createShaderProgram(String vertexShaderSource,
			String geometryShaderSource, String fragmentShaderSource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GvVertexBuffer createVertexBuffer(int sizeInBytes) {
		// TODO Auto-generated method stub
		return null;
	}

}

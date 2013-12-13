package de.grovie.engine.renderer.GL3;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.device.GvGraphicsWindow;
import de.grovie.engine.renderer.device.GvShaderProgram;
import de.grovie.engine.renderer.device.GvVertexBuffer;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvDeviceGL3 extends GvDevice{

	@Override
	public GvGraphicsWindow  createWindow(int width, 
			int height, 
			GvEventListener eventListener,
			GvWindowSystem windowSystem,
			String windowTitle) {
		
		GvWindowSystem winSys = windowSystem.getInstance(width, height, windowTitle, eventListener);
		
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

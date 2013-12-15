package de.grovie.engine.renderer;

import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.device.GvGraphicsWindow;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	GvWindowSystem lWindowSystem;
	String lWindowTitle;
	int lWindowWidth;
	int lWindowHeight;
	
	GvDevice lDevice;
	GvEventListener lEventListener;
	GvGraphicsWindow lGraphicsWindow;
	
	public GvRenderer(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight)
	{
		lWindowSystem = windowSystem;
		lWindowTitle = windowTitle;
		lWindowWidth = windowWidth;
		lWindowHeight = windowHeight;
		lThread = new Thread(this, "GroViE Renderer");
	}
	
	public void start()
	{
		lThread.start();
	}
	
	@Override
	public void run() {
		lDevice = createDevice();
		lEventListener = createEventListener();
		lGraphicsWindow = lDevice.createWindow(lWindowWidth, 
				lWindowHeight, 
				lEventListener,
				lWindowSystem,
				lWindowTitle);
	}
	
	public abstract GvDevice createDevice();
	public abstract GvEventListener createEventListener();
}

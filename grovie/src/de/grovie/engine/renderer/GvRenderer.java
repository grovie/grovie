package de.grovie.engine.renderer;

import de.grovie.engine.GvEngine;
import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	GvEngine lEngine;
	GvWindowSystem lWindowSystem;
	String lWindowTitle;
	
	GvDevice lDevice;
	GvEventListener lEventListener;
	
	public GvRenderer(GvEngine engine, GvWindowSystem windowSystem, String windowTitle)
	{
		lEngine = engine;
		lWindowSystem = windowSystem;
		lWindowTitle = windowTitle;
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
		lDevice.createWindow(640, 480, lEventListener,lWindowSystem,lWindowTitle);
	}
	
	public abstract GvDevice createDevice();
	public abstract GvEventListener createEventListener();
}

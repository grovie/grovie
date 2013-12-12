package de.grovie.engine.renderer;

import de.grovie.engine.GvEngine;

public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	GvEngine lEngine;
	
	GvDevice lDevice;
	GvEventListener lEventListener;
	
	public GvRenderer(GvEngine engine)
	{
		lEngine = engine;
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
		lDevice.createWindow(640, 480, lEventListener);
	}
	
	public abstract GvDevice createDevice();
	public abstract GvEventListener createEventListener();
}

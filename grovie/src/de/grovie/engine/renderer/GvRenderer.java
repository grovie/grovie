package de.grovie.engine.renderer;

import de.grovie.engine.renderer.device.GvDevice;
import de.grovie.engine.renderer.device.GvGraphicsWindow;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	//window system
	GvWindowSystem lWindowSystem;
	String lWindowTitle;
	
	//drawing objects
	GvDevice lDevice;
	GvEventListener lEventListener;
	GvGraphicsWindow lGraphicsWindow;
	
	//state machine that controls interaction with state of renderer
	GvRendererStateMachine lRendererStateMachine;
	
	public GvRenderer(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight)
	{
		lWindowSystem = windowSystem;
		lWindowTitle = windowTitle;
		
		lRendererStateMachine = new GvRendererStateMachine(windowWidth,windowHeight);

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
		lGraphicsWindow = lDevice.createWindow(
				lWindowSystem,
				this);
	}
	
	public GvRendererStateMachine getRendererStateMachine()
	{
		return lRendererStateMachine;
	}
	
	public GvEventListener getEventListener()
	{
		return lEventListener;
	}
	
	public String getWindowTitle()
	{
		return lWindowTitle;
	}
	
	public void redraw()
	{
		lGraphicsWindow.getWindowSystem().getCanvas().redraw();
	}
	
	public abstract GvDevice createDevice();
	public abstract GvEventListener createEventListener();
}

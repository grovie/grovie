package de.grovie.renderer;

import de.grovie.renderer.windowsystem.GvWindowSystem;

public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	//window system
	GvWindowSystem lWindowSystem;
	String lWindowTitle;
	
	//variables related to drawing 
	GvDevice lDevice;
	GvIllustrator lIllustrator;
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
		lIllustrator = createIllustrator();
		lGraphicsWindow = lDevice.createWindow(
				lWindowSystem,
				this);
	}
	
	public GvRendererStateMachine getRendererStateMachine()
	{
		return lRendererStateMachine;
	}
	
	public GvIllustrator getIllustrator()
	{
		return lIllustrator;
	}
	
	public String getWindowTitle()
	{
		return lWindowTitle;
	}
	
	public void refresh()
	{
		lGraphicsWindow.getWindowSystem().getCanvas().refresh();
	}
	
	public abstract GvDevice createDevice();
	public abstract GvIllustrator createIllustrator();
}

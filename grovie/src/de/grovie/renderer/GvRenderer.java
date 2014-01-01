package de.grovie.renderer;

import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class is an abstract renderer above any graphics API.
 * An instance of this class runs as an independent (rendering) 
 * thread from the other components of the GroViE engine.
 * 
 * @author yong
 *
 */
public abstract class GvRenderer implements Runnable {

	Thread lThread;
	
	//window system
	private GvWindowSystem lWindowSystem;
	private String lWindowTitle;
	
	//variables related to drawing 
	protected GvDevice lDevice;					//rendering device factory - objects can be shared between contexts
	protected GvContext lContext;				//rendering context factory
	private GvIllustrator lIllustrator;			//drawing mechanism and 3d pipelines
	private GvGraphicsWindow lGraphicsWindow;	//drawing canvas
	private GvAnimator lAnimator;				//looping mechanism for this rendering thread
	
	//state machine that controls interaction with state of renderer
	private GvRendererStateMachine lRendererStateMachine;
	
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
		lContext = createContext();
		lIllustrator = createIllustrator();
		lAnimator = createAnimator();
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
	
	/**
	 * Repaints or refreshes the drawing canvas
	 */
	public void refresh()
	{
		lGraphicsWindow.getWindowSystem().getCanvas().refresh();
	}
	
	public GvDevice getDevice()
	{
		return lDevice;
	}
	
	public GvAnimator getAnimator()
	{
		return lAnimator;
	}
	
	public abstract GvDevice createDevice();
	public abstract GvContext createContext();
	public abstract GvIllustrator createIllustrator();
	public abstract GvAnimator createAnimator();
}

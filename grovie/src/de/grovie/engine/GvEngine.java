package de.grovie.engine;

import de.grovie.data.GvData;
import de.grovie.data.GvDataGL2;
import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvThreadManager;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GL2.GvRendererGL2;
import de.grovie.renderer.windowsystem.GvWindowSystem;
import de.grovie.renderer.windowsystem.AWT.GvWindowSystemAWTGL;

/**
 * This class is the main class representing the visualization engine.
 * It operates in one of three modes - in memory, embedded or server.
 * The default operation is embedded, i.e. the engine runs with a database
 * embedded in the same application as the engine itself. 
 * The engine can also run in memory, i.e. without a database. In this mode,
 * the scene graph is constructed manually instead of synchronizing with the
 * database.
 * In server mode, the engine synchronizes its data with a remote database.
 * 
 * @author yong
 *
 */
public class GvEngine extends GvThreadManager{

	public enum GvWindowSystemLibrary
	{
		AWT_OPEN_GL;
	}
	
	public enum GvGraphicsAPI
	{
		OPEN_GL_2_0;
	}
	
	public enum GvEngineMode
	{
		IN_MEM,		//visualization engine without database 
		EMBEDDED, 	//visualization engine with embedded database - default mode
		SERVER		//visualization engine with database server
	}

	private static GvEngine lInstance;	//singleton instance of engine
	
	private GvEngineMode lMode;
	private GvWindowSystemLibrary lWindowSystemClass;
	private GvGraphicsAPI lRendererClass;
	private String lWindowTitle;
	
	private GvRenderer lRenderer; 	//renderer
	private GvData lData;			//data acceleration layer
	private GvDb lDb;				//graph database
	
	
	//communication queues between threads
	private GvMsgQueue<GvRenderer> lQueueRenderer;
	private GvMsgQueue<GvData> lQueueData;
	private GvMsgQueue<GvDb> lQueueDb;

	private GvEngine(GvEngineMode mode,
			GvWindowSystemLibrary windowSystemClass,
			GvGraphicsAPI rendererClass,
			int windowWidth,
			int windowHeight,
			String windowTitle)
	{
		//3 threads
		super(3);
		
		//member variables in engine
		lMode = mode;
		lWindowSystemClass = windowSystemClass;
		lRendererClass = rendererClass;
		lWindowTitle = windowTitle;
		
		//message queues
		lQueueRenderer = new GvMsgQueue<GvRenderer>();
		lQueueData = new GvMsgQueue<GvData>();
		lQueueDb = new GvMsgQueue<GvDb>();
		
		//engine components
		lRenderer = getRenderer(windowWidth,windowHeight);
		lData = getData();
	} 

	public static GvEngine getInstance(
			GvWindowSystemLibrary windowSystemClass,
			GvGraphicsAPI rendererClass,
			int windowWidth,
			int windowHeight,
			String windowTitle) 
	{
		if (lInstance == null) {
			lInstance = new GvEngine(GvEngineMode.EMBEDDED,
					windowSystemClass,
					rendererClass,
					windowWidth, 
					windowHeight,
					windowTitle);
		}
		return lInstance;
	}

	public static GvEngine getInstance(
			GvEngineMode mode, 
			GvWindowSystemLibrary windowSystemClass, 
			GvGraphicsAPI rendererClass,
			int windowWidth,
			int windowHeight,
			String windowTitle) 
	{
		return GvEngine.getInstance(GvEngineMode.EMBEDDED,
				windowSystemClass, rendererClass, windowWidth, windowHeight, windowTitle);
	}
	
	public GvEngineMode getMode()
	{
		return lMode;
	}
	
	/**
	 * Starts and initializes entire visualization engine.
	 * @throws GvExceptionEngineNoEventListener 
	 */
	public void start(String dbPathAbs)
	{
		start();
	}
	
	/**
	 * Stops and shutsdown entire visualization engine.
	 */
	public void stop()
	{
		shutdownAndAwaitTermination();
	}

	@Override
	public void start() {
		//start renderer thread service
		lThreadPool.execute(lRenderer);
		
		//start data thread service
		lThreadPool.execute(lData);
		
		//start database thread
		//lThreadPool.execute(lDb);
	}
	
	private GvWindowSystem getWindowSystem(GvWindowSystemLibrary winSysClass)
	{
		if(winSysClass == GvEngine.GvWindowSystemLibrary.AWT_OPEN_GL)
			return new GvWindowSystemAWTGL();
		
		return null;
	}
	
	private GvRenderer getRenderer(
			int windowWidth,
			int windowHeight)
	{
		if(lRendererClass == GvEngine.GvGraphicsAPI.OPEN_GL_2_0)
		{
			return new GvRendererGL2(getWindowSystem(lWindowSystemClass),
					lWindowTitle,
					windowWidth,
					windowHeight,
					lQueueRenderer,
					lQueueData
					);
		}
		return null;
	}
	
	private GvData getData()
	{
		if(lRendererClass == GvEngine.GvGraphicsAPI.OPEN_GL_2_0)
		{
			return new GvDataGL2(getWindowSystem(lWindowSystemClass),
					lQueueData,
					lQueueRenderer,
					lQueueDb);
		}
		return null;
	}
}

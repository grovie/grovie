package de.grovie.engine;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tinkerpop.blueprints.TransactionalGraph;

import de.grovie.data.GvData;
import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvMsgRenderShutdown;
import de.grovie.engine.concurrent.GvThreadManager;
import de.grovie.exception.GvExDbSceneDuplicated;
import de.grovie.exception.GvExDbUnrecognizedImpl;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GL2.GvRendererGL2;
import de.grovie.renderer.windowsystem.GvWindowSystem;
import de.grovie.renderer.windowsystem.AWT.GvWindowSystemAWTGL;
import de.grovie.renderer.windowsystem.swing.GvWindowSystemSwingGL;

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
		AWT_OPEN_GL,
		SWING_OPEN_GL;
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
	
	//data thread future - reference to scheduled task
	ScheduledFuture<?> lFutureData;
	
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
		//2 threads - for renderer and data
		super(2);
		
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
	 * @throws GvExDbUnrecognizedImpl 
	 * @throws GvExDbSceneDuplicated 
	 * @throws GvExceptionEngineNoEventListener 
	 */
	public void start(String dbPathAbs) throws GvExDbUnrecognizedImpl, GvExDbSceneDuplicated
	{
		//get instance of graph database using database path
		lDb = GvDb.getInstance(dbPathAbs);
		
		//provide the database class with thread communication message queues
		lDb.setQueues(lQueueDb, lQueueData);
		
		//start the renderer and data-accel threads
		start();
	}
	
	/**
	 * Stops and shutsdown entire visualization engine.
	 */
	public void stop()
	{
		//shutdown rendering thread
		lQueueRenderer.offer(new GvMsgRenderShutdown());
		System.out.println("Shutdown: Renderer thread");
		
		//shutdown data thread
		lFutureData.cancel(false); //cancel scheduled data thread execution
		System.out.println("Shutdown: Data thread");
		
		//shutdown database
		lDb.shutdown(); //shut down the graph databas
		System.out.println("Shutdown: Database");
				
		//shutdown thread pool
		shutdownAndAwaitTermination(); //terminate threads
		System.out.println("Shutdown: Thread pool");
		
		System.out.println("Engine shutdown complete");
	}

	@Override
	public void start() {
		//start renderer thread service
		lThreadPool.execute(lRenderer);
		
		//start data thread service
		lFutureData = lThreadPool.scheduleAtFixedRate(lData, 0, 16, TimeUnit.MILLISECONDS);
		
		//initialize static scene data, e.g. textures and materials
		lDb.initScene();
	}
	
	private GvWindowSystem getWindowSystem(GvWindowSystemLibrary winSysClass)
	{
		if(winSysClass == GvEngine.GvWindowSystemLibrary.AWT_OPEN_GL)
			return new GvWindowSystemAWTGL();
		if(winSysClass == GvEngine.GvWindowSystemLibrary.SWING_OPEN_GL)
			return new GvWindowSystemSwingGL();
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
			return new GvData(
					lQueueData,
					lQueueRenderer,
					lQueueDb);
		}
		return null;
	}
	
	public void simulationStep(String stepId)
	{
		lDb.simulationStep(stepId);
	}
	
	public TransactionalGraph getGraph()
	{
		return lDb.getGraph();
	}
}

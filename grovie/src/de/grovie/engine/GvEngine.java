package de.grovie.engine;

import de.grovie.data.GvData;
import de.grovie.db.GvDb;
import de.grovie.engine.renderer.GvRenderer;

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
public class GvEngine {

	public enum GvEngineMode
	{
		IN_MEM,		//visualization engine without database 
		EMBEDDED, 	//visualization engine with embedded database - default mode
		SERVER		//visualization engine with database server
	}

	private static GvEngine lInstance;	//singleton instance of engine

	private final GvEngineMode lMode;
	
	private GvDb lDb;		//graph database
	private GvData lData;	//scene graph
	
	private GvEngine()
	{
		lMode = GvEngineMode.EMBEDDED;
	} 

	private GvEngine(GvEngineMode mode)
	{
		this.lMode = mode;
	} 

	public static synchronized GvEngine getInstance() {
		if (lInstance == null) {
			lInstance = new GvEngine(GvEngineMode.EMBEDDED);
		}
		return lInstance;
	}

	public static synchronized GvEngine getInstance(GvEngineMode mode) {
		if (lInstance == null) {
			lInstance = new GvEngine(mode);
		}
		return lInstance;
	}
	
	public GvEngineMode getMode()
	{
		return lMode;
	}
	
	/**
	 * Starts and initializes entire visualization engine.
	 * @throws GvExceptionEngineNoEventListener 
	 */
	public void start(String dbPathAbs, GvRenderer renderer)
	{
		renderer.start();
	}
	
	/**
	 * Stops and shutsdown entire visualization engine.
	 */
	public void stop()
	{
		
	}
}

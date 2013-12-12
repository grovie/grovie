package de.grovie.db;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.grovie.exception.GvExceptionDbUnrecognizedImpl;

/**
 * This class provides database access to the package de.grovie.data.
 * Blueprints API is used as the java interface to so as to keep 
 * GroViE independent from the underlying graph database implementation.
 * 
 * @author yong
 */
public class GvDb {

	//types of database implementation (implementing Blueprints API)
	public enum GvDbImpl
	{
		NEO4J,
		INFINITE_GRAPH, //temp not in use
		ORACLE_NOSQL,	//temp not in use
		TITAN			//temp not in use
	}
	
	private final static GvDbImpl lGrovieDbImplDefault = GvDbImpl.NEO4J; 
	
	private static GvDb lInstance;			//singleton instance of GrovieDb
	private static GvDbImpl lGrovieDbImpl;	//database implementation (e.g. Neo4j, Titan, etc.)
	
	private Graph lGraph; //instance of database graph
	
	/**
	 * Constructor
	 * @param dbPathAbs
	 * @throws GvExceptionDbUnrecognizedImpl
	 */
	private GvDb(String dbPathAbs) throws GvExceptionDbUnrecognizedImpl
	{
		lGraph = createDb(dbPathAbs, lGrovieDbImplDefault);
		lGrovieDbImpl = lGrovieDbImplDefault;
	}

	/**
	 * Constructor
	 * @param dbPathAbs
	 * @param impl
	 * @throws GvExceptionDbUnrecognizedImpl
	 */
	private GvDb(String dbPathAbs, GvDbImpl impl) throws GvExceptionDbUnrecognizedImpl
	{
		try{
			lGraph = createDb(dbPathAbs, impl);
			lGrovieDbImpl = impl;
		}
		catch(GvExceptionDbUnrecognizedImpl err)
		{
			lGraph = createDb(dbPathAbs, lGrovieDbImplDefault);
			lGrovieDbImpl = lGrovieDbImplDefault;
		}
	}
	
	/**
	 * Creates database at the specified path using the specified database implementation.
	 * @param dbPathAbs
	 * @param impl
	 * @return instance of graph database
	 * @throws GvExceptionDbUnrecognizedImpl
	 */
	private Graph createDb(String dbPathAbs, GvDbImpl impl) throws GvExceptionDbUnrecognizedImpl
	{
		if(impl==GvDbImpl.NEO4J)
			return new Neo4jGraph(dbPathAbs);
		else
			throw new GvExceptionDbUnrecognizedImpl("GrovieExceptionDb unrecognized database implementation: " + impl);
	}

	/**
	 * Get singleton instance of GrovieDb
	 * @param dbPathAbs
	 * @return
	 * @throws GvExceptionDbUnrecognizedImpl
	 */
	public static GvDb getInstance(String dbPathAbs) throws GvExceptionDbUnrecognizedImpl {
		if (lInstance == null) {
			lInstance = new GvDb(dbPathAbs);
		}
		return lInstance;
	}

	/**
	 * Get singleton instance of GrovieDb
	 * @param dbPathAbs
	 * @param impl
	 * @return instance of GrovieDb
	 * @throws GvExceptionDbUnrecognizedImpl
	 */
	public static GvDb getInstance(String dbPathAbs, GvDbImpl impl) throws GvExceptionDbUnrecognizedImpl {
		if (lInstance == null) {
			lInstance = new GvDb(dbPathAbs, impl);
		}
		return lInstance;
	}
	
	/**
	 * Get the database implementation type.
	 * @return database implementation type (e.g. Neo4j, Titan, etc.)
	 */
	public static GvDbImpl getDbImpl()
	{
		return lGrovieDbImpl;
	}
	
	public Graph getGraph()
	{
		return lGraph;
	}
}

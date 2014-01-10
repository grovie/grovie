package de.grovie.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsgDataSceneStaticData;
import de.grovie.engine.concurrent.GvMsgDataSceneUpdate;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.exception.GvExDbSceneDuplicated;
import de.grovie.exception.GvExDbUnrecognizedImpl;
import de.grovie.renderer.GvMaterial;
import de.grovie.util.graph.GvGraphUtil;

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
	
	private TransactionalGraph lGraph; //instance of database graph
	private Vertex lVertexScene;
	
	//message queues - for communication with other threads
	GvMsgQueue<GvDb> lQueueIn;
	GvMsgQueue<GvData> lQueueOutData;
	
	/**
	 * Constructor
	 * @param dbPathAbs
	 * @throws GvExDbUnrecognizedImpl
	 * @throws GvExDbSceneDuplicated 
	 */
	private GvDb(String dbPathAbs) throws GvExDbUnrecognizedImpl, GvExDbSceneDuplicated
	{
		lGraph = createDb(dbPathAbs, lGrovieDbImplDefault);
		lGrovieDbImpl = lGrovieDbImplDefault;
		
		lVertexScene = GvGraphUtil.getVertexScene(lGraph);
	}

	/**
	 * Constructor
	 * @param dbPathAbs
	 * @param impl
	 * @throws GvExDbUnrecognizedImpl
	 * @throws GvExDbSceneDuplicated 
	 */
	private GvDb(String dbPathAbs, GvDbImpl impl) throws GvExDbUnrecognizedImpl, GvExDbSceneDuplicated
	{
		try{
			lGraph = createDb(dbPathAbs, impl);
			lGrovieDbImpl = impl;
			lVertexScene = GvGraphUtil.getVertexScene(lGraph);
		}
		catch(GvExDbUnrecognizedImpl err)
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
	 * @throws GvExDbUnrecognizedImpl
	 */
	private TransactionalGraph createDb(String dbPathAbs, GvDbImpl impl) throws GvExDbUnrecognizedImpl
	{
		if(impl==GvDbImpl.NEO4J)
			return new Neo4jGraph(dbPathAbs);
		else
			throw new GvExDbUnrecognizedImpl("GrovieExceptionDb unrecognized database implementation: " + impl);
	}

	/**
	 * Get singleton instance of GrovieDb
	 * @param dbPathAbs
	 * @return
	 * @throws GvExDbUnrecognizedImpl
	 * @throws GvExDbSceneDuplicated 
	 */
	public static GvDb getInstance(String dbPathAbs) throws GvExDbUnrecognizedImpl, GvExDbSceneDuplicated {
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
	 * @throws GvExDbUnrecognizedImpl
	 * @throws GvExDbSceneDuplicated 
	 */
	public static GvDb getInstance(String dbPathAbs, GvDbImpl impl) throws GvExDbUnrecognizedImpl, GvExDbSceneDuplicated {
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
	
	public TransactionalGraph getGraph()
	{
		return lGraph;
	}
	
	public void setQueues(GvMsgQueue<GvDb> queueDb, GvMsgQueue<GvData> queueData) 
	{
		lQueueIn = queueDb;
		lQueueOutData= queueData;
	}
	
	public void simulationStep(String stepId)
	{
		lQueueOutData.offer(new GvMsgDataSceneUpdate(stepId, lGraph));
	}
	
	public void initScene()
	{
		sendSceneStaticData();
		
		//send initial step id and graph reference to data layer
		lQueueOutData.offer(new GvMsgDataSceneUpdate("0", lGraph));
	}

	public void sendSceneStaticData() {
		ArrayList<GvMaterial> materials = new ArrayList<GvMaterial>();
		ArrayList<InputStream> textures = new ArrayList<InputStream>();
		ArrayList<String> textureFileExts = new ArrayList<String>();
		
		//FOR DEBUG - PATH

		//Materials
		GvMaterial materialDefault = new GvMaterial();
		GvMaterial greenMaterial = new GvMaterial();
		greenMaterial.lDiffuse[0] = 0.0f;
		greenMaterial.lDiffuse[1] = 1.0f;
		greenMaterial.lDiffuse[2] = 0.0f;

		//Textures
		//String filePath = "/Users/yongzhiong/Downloads/";
		String filePath = "C:\\Users\\yong\\GroViE\\textures\\";
		
		try {
			FileInputStream streamColors = new FileInputStream(new File(filePath + "test.jpg"));
			
			FileInputStream streamBark = new FileInputStream(new File(filePath +"BarkDecidious0164_5_thumbhuge.jpg"));
			
			materials.add(materialDefault);
			materials.add(greenMaterial);
			textures.add(streamColors);
			textureFileExts.add("jpg");
			textures.add(streamBark);
			textureFileExts.add("jpg");
			
			//streamColors.close();
			//streamBark.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		

		
		//END DEBUG
		
		//load materials from database
		
		
		
		//load textures and file extensions  from database
		
		
		//send to data layer
		lQueueOutData.offer(new GvMsgDataSceneStaticData(materials, textures, textureFileExts));
	}
	
	/**
	 * Shutsdown the graph database
	 */
	public void shutdown()
	{
		if(lGraph!=null)
		{
			lGraph.shutdown();
		}
	}
}

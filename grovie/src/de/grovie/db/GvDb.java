package de.grovie.db;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.Graph;
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
import de.grovie.util.file.FileResource;

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
		
		lVertexScene = getVertexScene();
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
			
			lVertexScene = getVertexScene();
		}
		catch(GvExDbUnrecognizedImpl err)
		{
			lGraph = createDb(dbPathAbs, lGrovieDbImplDefault);
			lGrovieDbImpl = lGrovieDbImplDefault;
			
			lVertexScene = getVertexScene();
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
	
	public Graph getGraph()
	{
		return lGraph;
	}
	
	public void setQueues(GvMsgQueue<GvDb> queueDb, GvMsgQueue<GvData> queueData) 
	{
		lQueueIn = queueDb;
		lQueueOutData= queueData;
	}
	
	public void simulationStep(int stepId)
	{
		lQueueOutData.offer(new GvMsgDataSceneUpdate(stepId, lGraph));
	}
	
	public void initScene()
	{
		sendSceneStaticData();
		
		//send initial step id and graph reference to data layer
		lQueueOutData.offer(new GvMsgDataSceneUpdate(0, lGraph));
	}

	public void sendSceneStaticData() {
		ArrayList<GvMaterial> materials = new ArrayList<GvMaterial>();
		ArrayList<InputStream> textures = new ArrayList<InputStream>();
		ArrayList<String> textureFileExts = new ArrayList<String>();
		
		//FOR DEBUG

		//Materials
		GvMaterial materialDefault = new GvMaterial();
		GvMaterial greenMaterial = new GvMaterial();
		greenMaterial.lDiffuse[0] = 0.0f;
		greenMaterial.lDiffuse[1] = 1.0f;
		greenMaterial.lDiffuse[2] = 0.0f;

		//Textures
		InputStream streamColors = FileResource.getResource(
				File.separator + "resources" + File.separator + "test" + File.separator + "texture" + 
						File.separator + "test.jpg");
		
		InputStream streamBark = FileResource.getResource(
				File.separator + "resources" + File.separator + "test" + File.separator + "texture" + 
						File.separator + "BarkDecidious0164_5_thumbhuge.jpg");
		
		materials.add(materialDefault);
		materials.add(greenMaterial);
		textures.add(streamColors);
		textureFileExts.add("jpg");
		textures.add(streamBark);
		textureFileExts.add("jpg");
		//END DEBUG
		
		//load materials from database
		
		
		
		//load textures and file extensions  from database
		
		
		//send to data layer
		lQueueOutData.offer(new GvMsgDataSceneStaticData(materials, textures, textureFileExts));
	}
	
	private Vertex getVertexScene() throws GvExDbSceneDuplicated
	{
		Iterable<Vertex> vertexSceneList = lGraph.getVertices("Type", "Scene");
		Iterator<Vertex> vertexSceneIterator = vertexSceneList.iterator();
		if(vertexSceneIterator.hasNext())
		{
			Vertex scene = vertexSceneIterator.next();
			
			if(vertexSceneIterator.hasNext())
			{
				throw new GvExDbSceneDuplicated("More than one scene in database");
			}
			
			return scene;
		}
		
		return null;
	}
}

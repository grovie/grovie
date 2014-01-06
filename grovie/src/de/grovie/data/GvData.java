package de.grovie.data;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsg;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.engine.concurrent.GvMsgRenderSceneStaticData;
import de.grovie.engine.concurrent.GvMsgRenderSwap;
import de.grovie.engine.concurrent.GvThread;
import de.grovie.exception.GvExEngineConcurrentThreadInitFail;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvRenderer;
import de.grovie.test.engine.renderer.TestRendererTex;
import de.grovie.util.graph.GvGraphUtil;

/**
 * This class performs CPU tasks that prepare data for visualization.
 * It communicates with the database (on another thread) and the
 * renderer (on another thread) via message queues.
 * 
 * @author yong
 *
 */
public class GvData extends GvThread {

	//message queues - for communication with other threads
	private GvMsgQueue<GvData> lQueueIn;
	private GvMsgQueue<GvRenderer> lQueueOutRenderer;

	//messages (reusable)
	private GvMsgRenderSwap lMsgRenderSwap;

	//draw group to be updated
	private GvDrawGroup lDrawGroup;

	//latest camera information
	private GvCamera lCamera;

	//latest simulation step and graph database reference
	private int lStepId;
	private TransactionalGraph lGraph;

	//latest set of processed data sent to renderer
	private int lLatestStepId;
	private GvCamera lLatestCamera;
	
	//FOR DEBUG
	int indices[];
	float vertices[];
	float normals[];
	GvGeometryTex geomBoxTex;
	GvGeometryTex geomTube;
	float verticesTube[];
	GvGeometryTex geomPoints;
	//END DEBUG
	
	public GvData() {
	}

	public GvData(
			GvMsgQueue<GvData> queueData,
			GvMsgQueue<GvRenderer> queueRenderer,
			GvMsgQueue<GvDb> queueDb) {

		lQueueIn = queueData;
		lQueueOutRenderer = queueRenderer;

		//reusable msg object
		lMsgRenderSwap = new GvMsgRenderSwap();

		//null until arrival in message queue from renderer thread 
		lDrawGroup = null;
		lCamera = null;

		//null until arrival in message queue from db thread
		lStepId = -1;
		lGraph = null;

		//tracking step id of latest  set of geometry sent to renderer 
		lLatestStepId = -1;
		//tracking camera info relevant to latest set of geometry sent to renderer
		lLatestCamera = new GvCamera();
		
		//FOR DEBUG
		//Test geometry
		//String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";
		String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\spheres.obj";
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);
		indices = geom.getIndices();
		vertices = geom.getVertices();
		normals = geom.getNormals();
		geomBoxTex = TestRendererTex.getTexturedBox();
		geomTube = TestRendererTex.getTube(1, 20, 10, 1);
		float tubev[] = geomTube.getVertices();
		int tubevcount = tubev.length;
		verticesTube = new float[tubevcount];
		for(int i=0; i< tubev.length; ++i)
		{
			verticesTube[i] = tubev[i];
		}
		geomPoints = TestRendererTex.getPoints(1000);
		//END DEBUG
	}

	@Override
	public void runThread() throws InterruptedException, GvExEngineConcurrentThreadInitFail 
	{	
		//thread loop
		for(;;)
		{
			//check for msgs from incoming queue
			GvMsg<GvData> msg = lQueueIn.poll();

			//if no messages, continue check
			if(msg==null)
			{
				continue;
			}
			else
			{	
				msg.process(this);
			}
		}
	}

	//standard out-going messages
	public void sendBufferSwap() {
		lQueueOutRenderer.offer(lMsgRenderSwap);
	}

	//incoming msg handlers
	public void receiveCameraUpdate(GvCamera camera) {
		lCamera = camera;
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry(); //TODO: need to use time buffer at msg queue handling to prevent flooding
	}

	public void receiveSceneUpdate(int stepId, TransactionalGraph graph)
	{
		lStepId = stepId;
		lGraph = graph;
		
		float tubev[] = geomTube.getVertices();
		int tubevcount = tubev.length/3;
		for(int i=0; i< tubevcount; ++i)
		{
			int indexOffset = i * 3;
			geomTube.setVertexValue(indexOffset+2, verticesTube[indexOffset+2]-(5*stepId));
		}
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry();
	}

	public void receiveBufferSet(GvDrawGroup drawGroup){
		lDrawGroup = drawGroup;
		
		//insert geometry into drawgroup buffer and send to renderer
		sendGeometry();
	}

	/**
	 * Inserts geometry into latest(if existing) update buffer received from renderer.
	 * 
	 * @return true if geometry is inserted successfully, false otherwise.
	 */
	private void sendGeometry() 
	{
		//check if any of the required items are missing for geometry insertion
		if((lDrawGroup==null)||(lCamera==null)||(lGraph==null)||(lStepId==-1))
			return;

		//check if any data has changed, or if camera has changed
		//if nothing has changed, no updated geometry needs to be sent to renderer
		if((lStepId == lLatestStepId)&&(lCamera.compare(lLatestCamera)))
			return;
		
		try{
			//TODO: acceleration structure updates, geometry generation and insertion
			
			//FOR DEBUG
			Vertex sceneVertex = GvGraphUtil.getVertexScene(lGraph);
			if(sceneVertex != null)
			{
	
				Vertex stepVertex = GvGraphUtil.getVertexStep(sceneVertex, lStepId);
				
				if(stepVertex!=null)
				{
					Vertex vertexInternode = null;
					
					Iterable<Vertex> internodeVertices = GvGraphUtil.getVerticesRefine(stepVertex);
					Iterator<Vertex> internodeVertexIter = internodeVertices.iterator();
					
					GvVisitorDraw visitorDraw = new GvVisitorDraw(lDrawGroup);
					
					while(internodeVertexIter.hasNext())
					{
						vertexInternode = internodeVertexIter.next();
						GvGraphUtil.traverseDepthFirst(vertexInternode, "Branch", visitorDraw);
					}
				}
			}
			
			
			GvBufferSet bufferSet;
			//send geometry to categorized draw groups //TODO: discard unnecessary listing of geometry in buffer sets
			bufferSet = lDrawGroup.getBufferSet(false, -1, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
			bufferSet.insertGeometry(vertices, normals, indices);
	
			bufferSet = lDrawGroup.getBufferSet(true, 0, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
			bufferSet.insertGeometry(geomBoxTex.getVertices(), geomBoxTex.getNormals(), geomBoxTex.getIndices(), geomBoxTex.getUv());
	
			bufferSet = lDrawGroup.getBufferSet(true, 1, 0, GvPrimitive.PRIMITIVE_TRIANGLE_STRIP, true);
			bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices(), geomTube.getUv());
			
			bufferSet = lDrawGroup.getBufferSet(false, -1, 1, GvPrimitive.PRIMITIVE_POINT, true);
			bufferSet.insertGeometry(geomPoints.getVertices(), geomPoints.getNormals(), geomPoints.getIndices());
			
			
			//END DEBUG
			
			
			//if geometry was inserted and sent to renderer,
			//set reference to draw-group null, wait for new reference from rendering thread
			lDrawGroup = null; 
			lLatestStepId = lStepId;
			lCamera.copyCamera(lLatestCamera);
			
			//send message to renderer to swap drawgroup buffers
			sendBufferSwap();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Sends static materials and textures to renderer for initialization.
	 * @param materials
	 * @param textures
	 */
	public void sendSceneStaticData(ArrayList<GvMaterial> materials,
			ArrayList<InputStream> textures,
			ArrayList<String> textureFileExts
			) {
		lQueueOutRenderer.offer(
				new GvMsgRenderSceneStaticData(materials, textures,textureFileExts));
	}
}

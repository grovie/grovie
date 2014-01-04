package de.grovie.data;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.db.GvDb;
import de.grovie.db.GvDbInteger;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;
import de.grovie.test.engine.renderer.TestRendererTex;

public class GvDataGL2 extends GvData {

	//FOR DEBUG
	int indices[];
	float vcopy[];
	float vertices[];
	float normals[];

	GvGeometryTex geomBoxTex;
	GvGeometryTex geomTube;
	GvGeometryTex geomPoints;
	
	int move;
	//END DEBUG
	
	public GvDataGL2(GvWindowSystem windowSystem,
			GvMsgQueue<GvData> lQueueData,
			GvMsgQueue<GvRenderer> lQueueRenderer, GvMsgQueue<GvDb> lQueueDb) 
	{
		super(windowSystem, lQueueData, lQueueRenderer, lQueueDb);
		
		//Test geometry
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);
		 indices = geom.getIndices();
		 vertices = geom.getVertices();
		 normals = geom.getNormals();

		 geomBoxTex = TestRendererTex.getTexturedBox();
		 geomTube = TestRendererTex.getTube(1, 20, 10, 1);
		 
		 vcopy = new float[geomTube.getVertices().length];
		 for(int i=0; i<geomTube.getVertices().length; ++i)
		 {
			 vcopy[i] = geomTube.getVertices()[i];
		 }
		 geomPoints = TestRendererTex.getPoints(1000);
		 
		 move = 0;
	}

	@Override
	public void receiveBufferSet(GvDrawGroup drawGroup) {
		lDrawGroup = drawGroup;

		//FOR DEBUG
		try
		{
			computeGeometry();
		}
		catch(Exception e)
		{
			System.out.println("error inserting test geometry");
		}
		//END DEBUG
		
		lDrawGroup = null; //set reference to draw-group null, wait for new reference from rendering thread
	}

	@Override
	public void receiveCameraUpdate(GvCamera camera) {
		lCamera = camera;
	}
	
	private void computeGeometry() throws GvExRendererDrawGroupRetrieval, GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		int numVertices = geomTube.getVertices().length/3;
		for(int i=0; i<numVertices; ++i)
		{
			int indexOffset = i*3;
			geomTube.setVertexValue(indexOffset+2, vcopy[indexOffset+2]-move*5.0f);
		}
		
		//send geom CPU buffers - simulate action 2 by foreign thread after receiving
		//msg to update buufers
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
	}

	@Override
	public void receiveSceneUpdate(GvDbInteger integer) {
		
		move = integer.getInt();
		System.out.println(move);
	}

}

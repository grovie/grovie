package de.grovie.data;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.db.GvDb;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;
import de.grovie.test.engine.renderer.TestRendererTex;

public class GvDataGL2 extends GvData {

	public GvDataGL2(GvWindowSystem windowSystem,
			GvMsgQueue<GvData> lQueueData,
			GvMsgQueue<GvRenderer> lQueueRenderer, GvMsgQueue<GvDb> lQueueDb) 
	{
		super(windowSystem, lQueueData, lQueueRenderer, lQueueDb);
	}

	@Override
	public void receiveBufferSet(GvDrawGroup drawGroup) {
		lDrawGroup = drawGroup;

		//FOR DEBUG
		try
		{
			insertTestGeometry();
		}
		catch(Exception e)
		{
			System.out.println("error inserting test geometry");
		}
		//END DEBUG
	}

	private void insertTestGeometry() throws GvExRendererDrawGroupRetrieval, GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//Test geometry
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);
		int indices[] = geom.getIndices();
		float vertices[] = geom.getVertices();
		float normals[] = geom.getNormals();

		GvGeometryTex geomBoxTex = TestRendererTex.getTexturedBox();
		GvGeometryTex geomTube = TestRendererTex.getTube(1, 20, 10, 1);
		GvGeometryTex geomPoints = TestRendererTex.getPoints(1000);

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
}

package de.grovie.renderer;

import java.util.ArrayList;

import de.grovie.engine.concurrent.GvMsgDataNewBufferSet;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;

/**
 * This class represents a set of buffers (vertex buffers, vertex arrays, index buffers)
 * that share a common texture, material and primitive type.
 * Geometry to be drawn are grouped by instances of this class to reduce
 * overhead of calls to the OpenGL server.
 * 
 * @author yong
 */
public abstract class GvBufferSet extends GvDrawGroup implements GvMsgDataNewBufferSet {
	
	public static long VBO_BLOCK_SIZE = 1048576; //TODO: put in resrc file
	public static long IBO_BLOCK_SIZE = 1048576; //TODO: put in resrc file
	
	//Wrappers for GPU objects (both server/client sides)
	protected ArrayList<GvVertexBuffer> lVertexBuffers;
	protected ArrayList<GvVertexArray> lVertexArrays;
	protected ArrayList<GvVertexArray> lVertexArraysToDelete;
	protected ArrayList<GvIndexBuffer> lIndexBuffers;
	
	//Geometry from foreign data thread
	protected ArrayList<float[]> lVertices;
	protected ArrayList<float[]> lNormals;
	protected ArrayList<int[]> lIndices;
	protected ArrayList<float[]> lUv;
	
	public GvBufferSet()
	{	
		lVertexBuffers = new ArrayList<GvVertexBuffer>();
		lVertexArrays = new ArrayList<GvVertexArray>();
		lVertexArraysToDelete = new ArrayList<GvVertexArray>();
		lIndexBuffers = new ArrayList<GvIndexBuffer>();
		
		lVertices = new ArrayList<float[]>();
		lNormals = new ArrayList<float[]>();
		lIndices = new ArrayList<int[]>();
		lUv = new ArrayList<float[]>();
	}
	
	public void insertGeometry(float[] vertices, float[] normals, int[] indices) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//insert in array buffers
		insertIntoArrayBuffers(vertices, normals);
		
		//insert in element buffer
		insertIntoElementBuffer(indices);
	}
	
	public void insertGeometry(float[] vertices, float[] normals, int[] indices, float[] uvcoords) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//insert in array buffers
		insertIntoArrayBuffers(vertices, normals, uvcoords);
		
		//insert in element buffer
		insertIntoElementBuffer(indices);
	}
	
	public ArrayList<GvVertexArray> getVertexArrays()
	{
		return this.lVertexArrays;
	}
	
	/**
	 * Called by data or updating thread to store CPU-side geometry data
	 * @param vertices
	 * @param normals
	 * @param uvcoords
	 */
	protected abstract void insertIntoArrayBuffers(float[] vertices, float[] normals, float[] uvcoords);
	
	/**
	 * Called by data or updating thread to store CPU-side geometry data
	 * @param vertices
	 * @param normals
	 */
	protected abstract void insertIntoArrayBuffers(float[] vertices, float[] normals);
	
	/**
	 * Called by data or updating thread to store CPU-side geometry indices
	 * @param indices
	 */
	protected abstract void insertIntoElementBuffer(int[] indices);
	
	/**
	 * Called by data or updating thread to clear GPU-server-side buffer objects 
	 * from another thread and context.
	 * @param context
	 */
	@Override
	public abstract void clear(GvRenderer renderer);
	
	/**
	 * Called by data or updating thread to update/load GPU-server-side buffer objects 
	 * from another thread and context.
	 * @param context
	 */
	@Override
	public abstract void update(GvRenderer renderer) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer;
	
	/**
	 * Called by renderering thread to construct VAOs
	 */
	@Override
	public abstract void updateVAO(GvRenderer renderer) throws GvExRendererVertexArray ;
}

package de.grovie.renderer;

import java.util.ArrayList;

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
public abstract class GvBufferSet extends GvDrawGroup{
	
	public static long VBO_BLOCK_SIZE = 1048576;
	public static long IBO_BLOCK_SIZE = 1048576;
	
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
	protected ArrayList<float[]> lMatrices;
	
	//Geometry instanced - reuse of (vertex,normal,index & uv) set with different matrix
	protected ArrayList<float[]> lInstanceMatrices;
	protected ArrayList<Integer> lInstanceSetIndices;
	
	/**
	 * Default constructor
	 */
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
		lMatrices = new ArrayList<float[]>();
		
		lInstanceMatrices = new ArrayList<float[]>();
		lInstanceSetIndices = new ArrayList<Integer>();
	}
	
	/**
	 * Adds a new set of geometry without texture coordinates into this buffer set.
	 * @param vertices
	 * @param normals
	 * @param indices
	 * @param matrixTransform
	 * @throws GvExRendererVertexBuffer
	 * @throws GvExRendererVertexArray
	 * @throws GvExRendererIndexBuffer
	 */
	public int insertGeometry(float[] vertices, float[] normals, int[] indices, float[] matrixTransform) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//insert in element buffer
		insertIntoElementBuffer(indices);
		
		//insert in array buffers
		return insertIntoArrayBuffers(vertices, normals, matrixTransform);
	}
	
	/**
	 * Adds a new set of geometry with texture coordinates into this buffer set.
	 * @param vertices
	 * @param normals
	 * @param indices
	 * @param uvcoords
	 * @param matrixTransform
	 * @throws GvExRendererVertexBuffer
	 * @throws GvExRendererVertexArray
	 * @throws GvExRendererIndexBuffer
	 */
	public int insertGeometry(float[] vertices, float[] normals, int[] indices, float[] uvcoords, float[] matrixTransform) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//insert in element buffer
		insertIntoElementBuffer(indices);
		
		//insert in array buffers
		return insertIntoArrayBuffers(vertices, normals, uvcoords, matrixTransform);
	}
	
	/**
	 * Adds a new set of instanced geometry into this buffer set.
	 * i.e. Re-use of vertices already added into this buffer set.
	 * @param setIndex referring to the set of vertices in the array of vertices in this buffer.
	 * @param matrixTransform
	 */
	public void insertGeometry(int setIndex, float[] matrixTransform)
	{
		lInstanceMatrices.add(matrixTransform);
		lInstanceSetIndices.add(new Integer(setIndex));
	}
	
	public ArrayList<GvVertexArray> getVertexArrays()
	{
		return this.lVertexArrays;
	}
	
	/**
	 * Inserts geometry info (vertices, normals, uv-coords) into lists
	 */
	protected int insertIntoArrayBuffers(float[] vertices, float[] normals, float[] uvcoords, float[] matrixTransform) 
	{
		lUv.add(uvcoords);
		return insertIntoArrayBuffers(vertices, normals, matrixTransform);
	}

	/**
	 * Inserts geometry info (vertices, normals) into lists
	 */
	protected int insertIntoArrayBuffers(float[] vertices, float[] normals, float[] matrixTransform) {
		lVertices.add(vertices);
		lNormals.add(normals);
		lMatrices.add(matrixTransform);
		return lVertices.size()-1;
	}

	/**
	 * Inserts geometry info (indices) into lists
	 */
	protected void insertIntoElementBuffer(int[] indices) {
		lIndices.add(indices);
	}
	
	public int getInstanceMatricesCount() {
		return lInstanceMatrices.size();
	}

	public int getInstanceSetIndicesCount() {
		return lInstanceSetIndices.size();
	}
	
	public float[] getInstanceMatrix(int index)
	{
		return lInstanceMatrices.get(index);
	}
	
	public int getInstanceSet(int index)
	{
		return lInstanceSetIndices.get(index);
	}
	
	/**
	 * Called by data or updating thread to clear GPU-server-side buffer objects 
	 * from another thread and context.
	 * @param context
	 */
	@Override
	public abstract void clear(Object libraryAPI);
	
	/**
	 * Called by data or updating thread to update/load GPU-server-side buffer objects 
	 * from another thread and context.
	 * @param context
	 */
	@Override
	public abstract void update(Object libraryAPI, GvDevice device) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer;
	
	/**
	 * Called by renderering thread to construct VAOs
	 */
	@Override
	public abstract void updateVAO(GvRenderer renderer) throws GvExRendererVertexArray ;
}

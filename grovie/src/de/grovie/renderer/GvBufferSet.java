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
public abstract class GvBufferSet extends GvDrawGroup {

	GvDevice lDevice;
	GvContext lContext;
	
	ArrayList<GvVertexBuffer> lVertexBuffers;
	ArrayList<GvVertexArray> lVertexArrays;
	ArrayList<GvIndexBuffer> lIndexBuffers;
	
	public GvBufferSet(GvDevice device, GvContext context)
	{
		lDevice = device;
		lContext = context;
		lVertexBuffers = new ArrayList<GvVertexBuffer>();
		lVertexArrays = new ArrayList<GvVertexArray>();
		lIndexBuffers = new ArrayList<GvIndexBuffer>();
	}
	
	public void addArrayBuffer() throws GvExRendererVertexBuffer, GvExRendererVertexArray
	{
		this.lVertexBuffers.add(lDevice.createVertexBuffer(1048576)); //TODO: read size from resource file
		this.lVertexArrays.add(lContext.createVertexArray());
	}
	
	public void addElementBuffer() throws GvExRendererIndexBuffer
	{
		this.lIndexBuffers.add(lDevice.createIndexBuffer(1048576)); //TODO: read size from rsrc file.
	}
	
	public void insertGeometry(float[] vertices, float[] normals, int[] indices) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//check if it is possible to insert vertices
		long sizeRequired = vertices.length * 4 + normals.length * 4;
		if(sizeRequired > 1048576)
			return;
		
		//check if it is possible to insert indices
		sizeRequired = indices.length * 4;
		if(sizeRequired > 1048576)
			return;
		
		boolean allocateNewBuffers = false;
		//check if vbo has sufficient mem
		GvVertexBuffer vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		long vboBytesFree = vbo.getSizeFree();
		if((vertices.length * 4) > vboBytesFree) //TODO: get float byte size from resource file
			allocateNewBuffers = true;
		
		//check if ibo has sufficient mem
		GvIndexBuffer ibo = lIndexBuffers.get(lIndexBuffers.size() - 1);
		long iboBytesFree = ibo.getSizeFree();
		if(indices.length * 4 > iboBytesFree)
			allocateNewBuffers = true;
		
		//allocate new buffers if necessary
		if(allocateNewBuffers)
		{
			addArrayBuffer(); //TODO: use thread msg queues to request this
			addElementBuffer();
		}
		
		//insert in array buffers
		insertIntoArrayBuffers(vertices, normals, null);
		
		//insert in element buffer
		insertIntoElementBuffer(indices);
	}
	
	public void insertGeometry(float[] vertices, float[] normals, int[] indices, float[] uvcoords) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		//flag to indicate if new buffers are necessary
		boolean allocateNewBuffers = false;
		
		//check if it is possible to insert vertices
		long sizeRequired = vertices.length * 4 + normals.length * 4 + uvcoords.length * 4;
		if(sizeRequired > 1048576)
			return;
		
		//check if vbo has sufficient mem
		GvVertexBuffer vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		long vboBytesFree = vbo.getSizeFree();
		if(sizeRequired > vboBytesFree) //TODO: get float byte size from resource file
			allocateNewBuffers = true;
		
		//check if it is possible to insert indices
		sizeRequired = indices.length * 4;
		if(sizeRequired > 1048576)
			return;
		
		//check if ibo has sufficient mem
		GvIndexBuffer ibo = lIndexBuffers.get(lIndexBuffers.size() - 1);
		long iboBytesFree = ibo.getSizeFree();
		if(sizeRequired > iboBytesFree)
			allocateNewBuffers = true;
		
		//allocate new buffers if necessary
		if(allocateNewBuffers)
		{
			addArrayBuffer(); //TODO: use thread msg queues to request this
			addElementBuffer();
		}
		
		//insert in array buffers
		insertIntoArrayBuffers(vertices, normals, null);
		
		//insert in element buffer
		insertIntoElementBuffer(indices);
	}
	
	protected abstract void insertIntoArrayBuffers(float[] vertices, float[] normals, float[] uvcoords);
	protected abstract void insertIntoElementBuffer(int[] indices);
}

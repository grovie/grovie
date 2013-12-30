package de.grovie.renderer;

import java.util.ArrayList;

/**
 * This class represents a set of buffers (vertex buffers, vertex arrays, index buffers)
 * that share a common texture, material and primitive type.
 * Geometry to be drawn are grouped by instances of this class to reduce
 * overhead of calls to the OpenGL server.
 * 
 * @author yong
 */
public class GvDrawGroupBufferSet extends GvDrawGroup {

	ArrayList<GvVertexBuffer> lVertexBuffers;
	ArrayList<GvVertexArray> lVertexArrays;
	ArrayList<GvIndexBuffer> lIndexBuffers;
	
	public GvDrawGroupBufferSet()
	{
		lVertexBuffers = new ArrayList<GvVertexBuffer>();
		lVertexArrays = new ArrayList<GvVertexArray>();
		lIndexBuffers = new ArrayList<GvIndexBuffer>();
	}
}

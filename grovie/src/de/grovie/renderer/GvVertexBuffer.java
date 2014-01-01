package de.grovie.renderer;

/**
 * This class represents a vertex buffer object. It refers to 
 * allocated memory in the GPU for vertex information.
 * 
 * @author yong
 *
 */
public class GvVertexBuffer extends GvBuffer {

	public GvVertexBuffer(int id, long size)
	{
		super(id,size);
	}
}

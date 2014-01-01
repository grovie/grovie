package de.grovie.renderer;

/**
 * This class represents an index buffer. It refers to allocated memory
 * in the GPU for containing indices that refer to the vertex buffer.
 * 
 * @author yong
 *
 */
public class GvIndexBuffer extends GvBuffer {
	
	public GvIndexBuffer(int id, long size)
	{
		super(id,size);
	}

}

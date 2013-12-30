package de.grovie.renderer;

/**
 * This class represents an index buffer. It refers to allocated memory
 * in the GPU for containing indices that refer to the vertex buffer.
 * 
 * @author yong
 *
 */
public class GvIndexBuffer {

	private int lId;		//id of vertex buffer
	private long lSize;		//size in bytes
	
	public GvIndexBuffer(int id, long size)
	{
		this.lId = id;
		this.lSize = size;
	}
	
	public int getId() {
		return lId;
	}
	public void setId(int id) {
		this.lId = id;
	}
	public long getSize() {
		return lSize;
	}
	public void setSize(long size) {
		this.lSize = size;
	}
}

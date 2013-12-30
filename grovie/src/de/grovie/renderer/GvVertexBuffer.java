package de.grovie.renderer;

/**
 * This class represents a vertex buffer object. It refers to 
 * allocated memory in the GPU for vertex information.
 * 
 * @author yong
 *
 */
public class GvVertexBuffer {

	private int lId;		//id of vertex buffer
	private long lSize;		//size in bytes
	
	public GvVertexBuffer(int id, long size)
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

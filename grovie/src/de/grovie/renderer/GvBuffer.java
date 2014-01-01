package de.grovie.renderer;

/**
 * This is a generic GPU memory buffer representation.
 * Used for representing vertex buffer objects or index buffer objects.
 * 
 * @author yong
 *
 */
public class GvBuffer {
	
	private int lId;		//id of vertex buffer
	private long lSize;		//size in bytes
	private long lSizeUsed;	//size in bytes used
	
	public GvBuffer(int id, long size)
	{
		this.lId = id;
		this.lSize = size;
		this.lSizeUsed = 0;
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
	public long getSizeUsed(){
		return lSizeUsed;
	}
	public void setSizeUsed(long sizeUsed){
		this.lSizeUsed = sizeUsed;
	}
	public long getSizeFree(){
		return lSize-lSizeUsed;
	}
}

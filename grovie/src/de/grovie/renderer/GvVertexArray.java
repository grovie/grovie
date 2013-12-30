package de.grovie.renderer;

/**
 * This class represents a vertex array object. It refers to a set
 * of client-side information (e.g. offset position into vertex buffer
 * , stride size in vertex buffer memory structure, etc.).
 * 
 * @author yong
 *
 */
public class GvVertexArray {

	private int lId;		//id of vertex buffer
	
	public GvVertexArray(int id)
	{
		this.lId = id;
	}
	
	public int getId() {
		return lId;
	}
	public void setId(int id) {
		this.lId = id;
	}
}

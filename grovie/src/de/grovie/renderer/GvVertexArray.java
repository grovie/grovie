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

	private int lId;		//id of vertex array
	
	private int lVboIndex;
	private long lVboOffset;
	private int lIboIndex;
	private long lIboOffset;
	
	private long lSizeVertices;
	private long lSizeNormals;
	private long lSizeUv;
	private int lSizeIndices;
	
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

	public int getVboIndex() {
		return lVboIndex;
	}

	public void setVboIndex(int vboIndex) {
		this.lVboIndex = vboIndex;
	}

	public long getVboOffset() {
		return lVboOffset;
	}

	public void setVboOffset(long vboOffset) {
		this.lVboOffset = vboOffset;
	}

	public int getIboIndex() {
		return lIboIndex;
	}

	public void setIboIndex(int iboIndex) {
		this.lIboIndex = iboIndex;
	}

	public long getIboOffset() {
		return lIboOffset;
	}

	public void setIboOffset(long iboOffset) {
		this.lIboOffset = iboOffset;
	}

	public long getSizeVertices() {
		return lSizeVertices;
	}

	public void setSizeVertices(long sizeVertices) {
		this.lSizeVertices = sizeVertices;
	}

	public long getSizeNormals() {
		return lSizeNormals;
	}

	public void setSizeNormals(long sizeNormals) {
		this.lSizeNormals = sizeNormals;
	}

	public long getSizeUv() {
		return lSizeUv;
	}

	public void setSizeUv(long sizeUv) {
		this.lSizeUv = sizeUv;
	}

	public int getSizeIndices() {
		return lSizeIndices;
	}

	public void setSizeIndices(int sizeIndices) {
		this.lSizeIndices = sizeIndices;
	}
}

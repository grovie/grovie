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
	
	private float[] lMatrix;
	
	public GvVertexArray(int id)
	{
		this.lId = id;
	}
	
	public GvVertexArray(int id,
			int vboIndex,
			long vboOffset,
			int iboIndex,
			long iboOffset,
			long sizeVertices,
			long sizeNormals,
			long sizeUv,
			int sizeIndices,
			float[] matrix
			)
	
	{
		this.lId = id;
		setData(vboIndex,vboOffset,iboIndex,iboOffset,sizeVertices,
				sizeNormals,sizeUv,sizeIndices,matrix);
	}
	
	public GvVertexArray(int id, GvVertexArray vaoInstance, float[] matrix)
	{
		this.lId = id;
		copyDataWithoutMatrix(vaoInstance);
		setMatrix(matrix);
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
	
	public float[] getMatrix()
	{
		return this.lMatrix;
	}
	
	public void setMatrix(float[] matrix)
	{
		this.lMatrix = matrix;
	}
	
	public void setData(int vboIndex,
			long vboOffset,
			int iboIndex,
			long iboOffset,
			long sizeVertices,
			long sizeNormals,
			long sizeUv,
			int sizeIndices,
			float[] matrix)
	{
		setVboIndex(vboIndex);
		setVboOffset(vboOffset);
		setIboIndex(iboIndex);
		setIboOffset(iboOffset);
		setSizeVertices(sizeVertices);
		setSizeNormals(sizeNormals);
		setSizeUv(sizeUv);
		setSizeIndices(sizeIndices);
		setMatrix(matrix);
	}
	
	public void copyDataWithoutMatrix(GvVertexArray vao)
	{
		setVboIndex(vao.getVboIndex());
		setVboOffset(vao.getVboOffset());
		setIboIndex(vao.getIboIndex());
		setIboOffset(vao.getIboOffset());//used in draw call
		setSizeVertices(vao.getSizeVertices());
		setSizeNormals(vao.getSizeNormals());
		setSizeUv(vao.getSizeUv());
		setSizeIndices(vao.getSizeIndices());
	}
}

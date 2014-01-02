package de.grovie.data.object;

public class GvGeometryTex extends GvGeometry {

	private float[] lUv;
	
	public GvGeometryTex(int sizeVertices, int sizeNormals, int sizeIndices, int sizeUv)
	{
		this.initVertices(sizeVertices);
		this.initNormals(sizeNormals);
		this.initIndices(sizeIndices);
		this.initUv(sizeUv);
	}
	
	public GvGeometryTex(float[] vertices, float[] normals, int[] indices, float[] uv)
	{
		this.lVertices = vertices;
		this.lNormals = normals;
		this.lIndices = indices;
		this.lUv = uv;
	}
	
	public void initUv(int size)
	{
		lUv = new float[size];
	}
	
	public void setUvValue(int index, float value)
	{
		lUv[index] = value;
	}
	
	public float[] getUv()
	{
		return lUv;
	}
}

package de.grovie.data.object;

public class GvGeometry {

	private float[] lVertices;
	private float[] lNormals;
	private int lIndices[];
	
	public GvGeometry()
	{
	}
	
	public void initVertices(int size)
	{
		lVertices = new float[size];
	}
	
	public void initNormals(int size)
	{
		lNormals = new float[size];
	}
	
	public void initIndices(int size)
	{
		lIndices = new int[size];
	}
	
	public void setVertexValue(int index, float value)
	{
		lVertices[index] = value;
	}
	
	public void setNormalValue(int index, float value)
	{
		lNormals[index] = value;
	}
	
	public void setIndexValue(int index, int value)
	{
		lIndices[index] = value;
	}
	
	public float[] getVertices()
	{
		return lVertices;
	}
	
	public float[] getNormals()
	{
		return lNormals;
	}
	
	public int[] getIndices()
	{
		return lIndices;
	}
}

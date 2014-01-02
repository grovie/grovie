package de.grovie.renderer;

public class GvLight {

	//TODO: put in resource file
	public static final float[] DEFAULT_POSITION = new float[]{0.57735026919f,0.57735026919f,0.57735026919f};
	public static final float[] DEFAULT_AMBIENT = new float[]{0.1f,0.1f,0.1f,1.0f};
	public static final float[] DEFAULT_DIFFUSE = new float[]{0.7f,0.7f,0.7f,1.0f};
	public static final float[] DEFAULT_SPECULAR = new float[]{0.7f,0.7f,0.7f,1.0f};
	
	public float[] lPosition;
	public float[] lAmbient;
	public float[] lDiffuse;
	public float[] lSpecular;
	
	public GvLight()
	{
		lPosition = new float[]{DEFAULT_POSITION[0],
				DEFAULT_POSITION[1],
				DEFAULT_POSITION[2]};
		initDefaultColors();
	}
	
	public GvLight(float posX, float posY, float posZ)
	{
		lPosition = new float[]{posX,posY,posZ};
		initDefaultColors();
	}
	
	private void initDefaultColors()
	{
		lAmbient = new float[]{DEFAULT_AMBIENT[0],
				DEFAULT_AMBIENT[1],
				DEFAULT_AMBIENT[2],
				DEFAULT_AMBIENT[3]};
		lDiffuse = new float[]{DEFAULT_DIFFUSE[0],
				DEFAULT_DIFFUSE[1],
				DEFAULT_DIFFUSE[2],
				DEFAULT_DIFFUSE[3]};
		lSpecular = new float[]{DEFAULT_SPECULAR[0],
				DEFAULT_SPECULAR[1],
				DEFAULT_SPECULAR[2],
				DEFAULT_SPECULAR[3]};
	}
	
	public void copyLight(GvLight anotherLight)
	{
		anotherLight.lPosition[0] = lPosition[0];
		anotherLight.lPosition[1] = lPosition[1];
		anotherLight.lPosition[2] = lPosition[2];
		
		anotherLight.lAmbient[0] = lAmbient[0];
		anotherLight.lAmbient[1] = lAmbient[1];
		anotherLight.lAmbient[2] = lAmbient[2];
		anotherLight.lAmbient[3] = lAmbient[3];
		
		anotherLight.lDiffuse[0] = lDiffuse[0];
		anotherLight.lDiffuse[1] = lDiffuse[1];
		anotherLight.lDiffuse[2] = lDiffuse[2];
		anotherLight.lDiffuse[3] = lDiffuse[3];
		
		anotherLight.lSpecular[0] = lSpecular[0];
		anotherLight.lSpecular[1] = lSpecular[1];
		anotherLight.lSpecular[2] = lSpecular[2];
		anotherLight.lSpecular[3] = lSpecular[3];
	}
}

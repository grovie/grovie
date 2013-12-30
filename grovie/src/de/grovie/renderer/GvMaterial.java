package de.grovie.renderer;

public class GvMaterial {

	public static final float[] DEFAULT_AMBIENT = new float[]{0.1f,0.1f,0.1f,1.0f};
	public static final float[] DEFAULT_DIFFUSE = new float[]{0.7f,0.7f,0.7f,1.0f};
	public static final float[] DEFAULT_SPECULAR = new float[]{0.2f,0.2f,0.2f,1.0f};
	public static final float DEFAULT_SHININESS = 0.1f;
	
	public float[] lAmbient;
	public float[] lDiffuse;
	public float[] lSpecular;
	public float lShininess;
	
	public GvMaterial()
	{
		lShininess = DEFAULT_SHININESS;
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
	
	public GvMaterial(float ambi0, float ambi1, float ambi2, float ambi3,
			float diff0, float diff1, float diff2, float diff3,
			float spec0, float spec1, float spec2, float spec3,
			float shin
			)
	{
		lShininess = shin;
		lAmbient = new float[]{ambi0,ambi1,ambi2,ambi3};
		lDiffuse = new float[]{diff0,diff1,diff2,diff3};
		lSpecular = new float[]{spec0,spec1,spec2,spec3};
	}
		
	public void copyMaterial(GvMaterial anotherMat)
	{
		anotherMat.lShininess = lShininess;
		
		anotherMat.lAmbient[0] = lAmbient[0];
		anotherMat.lAmbient[1] = lAmbient[1];
		anotherMat.lAmbient[2] = lAmbient[2];
		anotherMat.lAmbient[3] = lAmbient[3];
		
		anotherMat.lDiffuse[0] = lDiffuse[0];
		anotherMat.lDiffuse[1] = lDiffuse[1];
		anotherMat.lDiffuse[2] = lDiffuse[2];
		anotherMat.lDiffuse[3] = lDiffuse[3];
		
		anotherMat.lSpecular[0] = lSpecular[0];
		anotherMat.lSpecular[1] = lSpecular[1];
		anotherMat.lSpecular[2] = lSpecular[2];
		anotherMat.lSpecular[3] = lSpecular[3];
	}
}

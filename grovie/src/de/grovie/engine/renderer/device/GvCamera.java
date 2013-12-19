package de.grovie.engine.renderer.device;

import org.apache.commons.math3.complex.Quaternion;

public class GvCamera {

	
	public static final float[] DEFAULT_POSITION = new float[]{0,0,5.0f};
	public static final float[] DEFAULT_UP = new float[]{0,1,0};
	public static final float[] DEFAULT_VIEW = new float[]{0,0,-1.0f};
	public static final float DEFAULT_FOV = 60.0f;
	public static final float DEFAULT_ASPECT = 1.0f;
	public static final float DEFAULT_NEAR = 0.1f;
	public static final float DEFAULT_FAR = 1000.0f;
	
	public float lPosition[];
	public float lUp[];
	public float lView[];
	
	public float lFov;
	public float lAspect;
	public float lNear;
	public float lFar;

	public GvCamera()
	{
		lPosition = new float[]{DEFAULT_POSITION[0],
				DEFAULT_POSITION[1],
				DEFAULT_POSITION[2]};
		lUp = new float[]{DEFAULT_UP[0],
				DEFAULT_UP[1],
				DEFAULT_UP[2]};
		lView = new float[]{DEFAULT_VIEW[0],
				DEFAULT_VIEW[1],
				DEFAULT_VIEW[2]};
	
		lFov = DEFAULT_FOV;
		lAspect = DEFAULT_ASPECT;
		lNear = DEFAULT_NEAR;
		lFar = DEFAULT_FAR;
	}
	
	public GvCamera(float aspect)
	{	
		lPosition = new float[]{DEFAULT_POSITION[0],
				DEFAULT_POSITION[1],
				DEFAULT_POSITION[2]};
		lUp = new float[]{DEFAULT_UP[0],
				DEFAULT_UP[1],
				DEFAULT_UP[2]};
		lView = new float[]{DEFAULT_VIEW[0],
				DEFAULT_VIEW[1],
				DEFAULT_VIEW[2]};
	
		lFov = DEFAULT_FOV;
		lAspect = aspect;
		lNear = DEFAULT_NEAR;
		lFar = DEFAULT_FAR;
	}
	
	public void copyCamera(GvCamera anotherCamera)
	{
		anotherCamera.lPosition[0] = lPosition[0];
		anotherCamera.lPosition[1] = lPosition[1];
		anotherCamera.lPosition[2] = lPosition[2];
		
		anotherCamera.lUp[0] = lUp[0];
		anotherCamera.lUp[1] = lUp[1];
		anotherCamera.lUp[2] = lUp[2];
		
		anotherCamera.lView[0] = lView[0];
		anotherCamera.lView[1] = lView[1];
		anotherCamera.lView[2] = lView[2];
		
		anotherCamera.lFov = lFov;
		anotherCamera.lAspect = lAspect;
		anotherCamera.lNear = lNear;
		anotherCamera.lFar = lFar;
	}

	public void setView(float x, float y, float z)
	{
		lView[0] = x;
		lView[1] = y;
		lView[2] = z;
	}
	
	public void setPosition(float x, float y, float z)
	{
		lPosition[0] = x;
		lPosition[1] = y;
		lPosition[2] = z;
	}
	
	public void setUp(float x, float y, float z)
	{
		lUp[0] = x;
		lUp[1] = y;
		lUp[2] = z;
	}
	
	public float getFov()
	{
		return lFov;
	}
	
	public void setFov(float fov)
	{
		this.lFov = fov;
	}
	
	public float getAspect() {
		return lAspect;
	}

	public void setAspect(float aspect) {
		this.lAspect = aspect;
	}

	public float getNear() {
		return lNear;
	}

	public void setNear(float near) {
		this.lNear = near;
	}

	public float getFar() {
		return lFar;
	}

	public void setFar(float far) {
		this.lFar = far;
	}
	
	/**
	 * Rotates camera view direction vector by angle along specified axis
	 * @param view
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 * @return Quaternion with rotated camera view direction
	 */
	public static Quaternion rotateCameraView(float[] view, double angle, double x, double y, double z)
	{
		Quaternion temp, quat_view, result;

		double temp_vector_com = Math.sin(angle/2.0);
		temp = new Quaternion(Math.cos(angle/2.0),
				x * temp_vector_com,
				y * temp_vector_com,
				z * temp_vector_com
				);

		quat_view = new Quaternion(0, view[0], view[1], view[2]);

		result = Quaternion.multiply(Quaternion.multiply(temp, quat_view), temp.getConjugate());

		return result;
	}

	/**
	 * Rotates camera view direction vector by angle along specified axis
	 * @param view
	 * @param angle
	 * @param x
	 * @param y
	 * @param z
	 * @return Quaternion with rotated camera view direction
	 */
	public static Quaternion rotateCameraView(double[] view, double angle, double x, double y, double z)
	{
		float[] floatArray = new float[view.length];
		for (int i = 0 ; i < view.length; i++)
		{
		    floatArray[i] = (float) view[i];
		}
		
		return rotateCameraView(floatArray, angle, x, y, z);
	}

	
}

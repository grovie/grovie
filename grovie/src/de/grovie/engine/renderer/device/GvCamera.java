package de.grovie.engine.renderer.device;

import org.apache.commons.math3.complex.Quaternion;

public class GvCamera {

	public float lPosition[];
	public float lUp[];
	public float lView[];

	public GvCamera()
	{
		lPosition = new float[]{0,0,5.0f};
		lUp = new float[]{0,1,0};
		lView = new float[]{0,0,-1.0f};
	}

	public void setView(float x, float y, float z)
	{
		lView[0] = x;
		lView[1] = y;
		lView[2] = z;
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

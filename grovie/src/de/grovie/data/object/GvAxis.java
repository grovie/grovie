package de.grovie.data.object;

import org.apache.commons.math3.linear.RealMatrix;

/**
 * Axis length, radius, orientation matrix and geometric error for LOD.
 * Computed and cached based on rendering algorithm at run-time.
 * 
 * @author yong
 *
 */
public class GvAxis {

	private RealMatrix lMatrix;
	private float lLength;
	private float lRadius;
	private float lError;
	
	public GvAxis(RealMatrix matrix)
	{
		lMatrix = matrix;
		lLength = -1;
		lRadius = -1;
		lError = 0;
	}
	
	public RealMatrix getMatrix() {
		return lMatrix;
	}
	public void setMatrix(RealMatrix matrix) {
		this.lMatrix = matrix;
	}
	public float getLength() {
		return lLength;
	}
	public void setLength(float length) {
		this.lLength = length;
	}
	public float getRadius() {
		return lRadius;
	}
	public void setRadius(float radius) {
		this.lRadius = radius;
	}
	public float getError() {
		return lError;
	}
	public void setError(float error) {
		this.lError = error;
	}
}

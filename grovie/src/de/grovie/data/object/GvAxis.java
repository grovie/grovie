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
	private float lErrorXY;
	private float lErrorZY;
	private float lErrorXZ;
	private float lErrorBifuration;
	private double[] lPtEnd; //world coordinates of end of axis
	
	public GvAxis(RealMatrix matrix)
	{
		lMatrix = matrix;
		lLength = -1;
		lRadius = -1;
		lError = 0;
		lErrorBifuration = 0;
		double[][] matrixData = matrix.getData();
		lPtEnd = new double[]{matrixData[0][3],matrixData[1][3],matrixData[2][3]};
		lErrorXY=0;
		lErrorZY=0;
		lErrorXZ=0;
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

	public float getErrorBifuration() {
		return lErrorBifuration;
	}

	public void setErrorBifuration(float errorBifuration) {
		this.lErrorBifuration = errorBifuration;
	}

	public double[] getPtEnd() {
		return lPtEnd;
	}

	public void setPtEnd(double x, double y, double z) {
		this.lPtEnd[0]=x;
		this.lPtEnd[1]=y;
		this.lPtEnd[2]=z;
	}

	public float getErrorXY() {
		return lErrorXY;
	}

	public void setErrorXY(float errorXY) {
		this.lErrorXY = errorXY;
	}

	public float getErrorZY() {
		return lErrorZY;
	}

	public void setErrorZY(float errorZY) {
		this.lErrorZY = errorZY;
	}

	public float getErrorXZ() {
		return lErrorXZ;
	}

	public void setErrorXZ(float errorXZ) {
		this.lErrorXZ = errorXZ;
	}
}

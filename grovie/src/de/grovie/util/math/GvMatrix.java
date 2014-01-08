package de.grovie.util.math;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class provides matrices commonly used. 
 * Some matrices are pre-computed to reduce real-time computations.
 * 
 * @author yong
 *
 */
public class GvMatrix {

	//precomputed rotation matrices, 1 degree resolution
	public static final RealMatrix[] kRotationRU = getMatrixRotationPrecomputeRU();
	public static final RealMatrix[] kRotationRH = getMatrixRotationPrecomputeRH();
	public static final RealMatrix[] kRotationRL = getMatrixRotationPrecomputeRL();
	
	/**
	 * Returns rotation matrix about axis with specified angle in degrees.
	 * Axis parameters should be normalized.
	 * 
	 * @param angle
	 * @param axisX
	 * @param axisY
	 * @param axisZ
	 * @return rotation matrix
	 */
	public static RealMatrix getMatrixRotation(float angle, float axisX, float axisY, float axisZ)
	{
		double angleRadians = angle / 180.0f * Math.PI;
		double cosAngle = Math.cos(angleRadians);
		double sinAngle = Math.sin(angleRadians);
		double oneMinusCosAngle = 1-cosAngle;
		
		double[][] matrix = new double[][]{
				{   //row 1
					axisX*axisX*oneMinusCosAngle + cosAngle,
					axisY*axisX*oneMinusCosAngle - axisZ*sinAngle,
					axisZ*axisX*oneMinusCosAngle + axisY*sinAngle,
					0
				},
				{	//row 2
					axisX*axisY*oneMinusCosAngle + axisZ*sinAngle,
					axisY*axisY*oneMinusCosAngle + cosAngle,
					axisZ*axisY*oneMinusCosAngle - axisX*sinAngle,
					0
				}, 
				{	//row 3
					axisX*axisZ*oneMinusCosAngle - axisY*sinAngle,
					axisY*axisZ*oneMinusCosAngle + axisX*sinAngle,
					axisZ*axisZ*oneMinusCosAngle + cosAngle,
					0
				}, 
				{	//row 4
					0,0,0,1
				}  
			};
		
		return new Array2DRowRealMatrix(matrix);
	}
	
	public static RealMatrix getMatrixTranslation(float x, float y, float z)
	{		
		double[][] matrix = new double[][]{
				{   //row 1
					1,0,0,x
				},
				{	//row 2
					0,1,0,y
				}, 
				{	//row 3
					0,0,1,z
				}, 
				{	//row 4
					0,0,0,1
				}  
			};
		
		return new Array2DRowRealMatrix(matrix);
	}
	
	public static RealMatrix getMatrixScale(float x, float y, float z)
	{
		double[][] matrix = new double[][]{
				{   //row 1
					x,0,0,0
				},
				{	//row 2
					0,y,0,0
				}, 
				{	//row 3
					0,0,z,0
				}, 
				{	//row 4
					0,0,0,1
				}  
			};
		
		return new Array2DRowRealMatrix(matrix);
	}

	public static RealMatrix getMatrixRotationRU(float angle)
	{
		return getMatrixRotation(angle, 0, 0, -1);
	}
	
	public static RealMatrix getMatrixRotationRH(float angle)
	{
		return getMatrixRotation(angle, 0, 1, 0);
	}
	
	public static RealMatrix getMatrixRotationRL(float angle)
	{
		return getMatrixRotation(angle, -1, 0, 0);
	}
	
	private static RealMatrix[] getMatrixRotationPrecomputeRU() {
		
		RealMatrix[] matrices = new RealMatrix[361];
		for(int i=0; i<361; ++i)
		{
			matrices[i] = getMatrixRotationRU(i);
		}
		return matrices;
	}
	

	private static RealMatrix[] getMatrixRotationPrecomputeRL() {
		
		RealMatrix[] matrices = new RealMatrix[361];
		for(int i=0; i<361; ++i)
		{
			matrices[i] = getMatrixRotationRL(i);
		}
		return matrices;
	}

	private static RealMatrix[] getMatrixRotationPrecomputeRH() {
		
		RealMatrix[] matrices = new RealMatrix[361];
		for(int i=0; i<361; ++i)
		{
			matrices[i] = getMatrixRotationRH(i);
		}
		return matrices;
	}
}

package de.grovie.util.math;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

/**
 * This class provides 4x4 transformation matrices. 
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
	public static final Vector3D kUpVector = new Vector3D(new double[]{0,1,0});
	public static final Vector3D kRightVector = new Vector3D(new double[]{1,0,0});
	public static final Vector3D kTowardVector = new Vector3D(new double[]{0,0,-1});
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
	
	public static void convertRowMajorToColumnMajor(double[] rowMajorMatrix)
	{
		double xx = rowMajorMatrix[0];
		double yx = rowMajorMatrix[4];
		double zx = rowMajorMatrix[8];
		double wx = rowMajorMatrix[12];
		
		double xy = rowMajorMatrix[1];
		double yy = rowMajorMatrix[5];
		double zy = rowMajorMatrix[9];
		double wy = rowMajorMatrix[13];
		
		double xz = rowMajorMatrix[2];
		double yz = rowMajorMatrix[6];
		double zz = rowMajorMatrix[10];
		double wz = rowMajorMatrix[14];
		
		double xw = rowMajorMatrix[3];
		double yw = rowMajorMatrix[7];
		double zw = rowMajorMatrix[11];
		double ww = rowMajorMatrix[15];
		
		rowMajorMatrix[0] = xx;
		rowMajorMatrix[1] = yx;
		rowMajorMatrix[2] = zx;
		rowMajorMatrix[3] = wx;
		
		rowMajorMatrix[4] = xy;
		rowMajorMatrix[5] = yy;
		rowMajorMatrix[6] = zy;
		rowMajorMatrix[7] = wy;
		
		rowMajorMatrix[8] = xz;
		rowMajorMatrix[9] = yz;
		rowMajorMatrix[10] = zz;
		rowMajorMatrix[11] = wz;
		
		rowMajorMatrix[12] = xw;
		rowMajorMatrix[13] = yw;
		rowMajorMatrix[14] = zw;
		rowMajorMatrix[15] = ww;
	}
	
	public static float[] convertRowMajorToColumnMajor(double[][] rowMajorMatrix)
	{
		float result[] = new float[16];
		
		float xx = (float) rowMajorMatrix[0][0];
		float yx = (float) rowMajorMatrix[1][0];
		float zx = (float) rowMajorMatrix[2][0];
		float wx = (float) rowMajorMatrix[3][0];
		
		float xy = (float) rowMajorMatrix[0][1];
		float yy = (float) rowMajorMatrix[1][1];
		float zy = (float) rowMajorMatrix[2][1];
		float wy = (float) rowMajorMatrix[3][1];
		
		float xz = (float) rowMajorMatrix[0][2];
		float yz = (float) rowMajorMatrix[1][2];
		float zz = (float) rowMajorMatrix[2][2];
		float wz = (float) rowMajorMatrix[3][2];
		
		float xw = (float) rowMajorMatrix[0][3];
		float yw = (float) rowMajorMatrix[1][3];
		float zw = (float) rowMajorMatrix[2][3];
		float ww = (float) rowMajorMatrix[3][3];
		
		result[0] = xx;
		result[1] = yx;
		result[2] = zx;
		result[3] = wx;
		
		result[4] = xy;
		result[5] = yy;
		result[6] = zy;
		result[7] = wy;
		
		result[8] = xz;
		result[9] = yz;
		result[10] = zz;
		result[11] = wz;
		
		result[12] = xw;
		result[13] = yw;
		result[14] = zw;
		result[15] = ww;
		
		return result;
	}
	
	public static float[] getIdentity()
	{
		float result[] = new float[16];
		
		result[0] = 1;
		result[1] = 0;
		result[2] = 0;
		result[3] = 0;
		
		result[4] = 0;
		result[5] = 1;
		result[6] = 0;
		result[7] = 0;
		
		result[8] = 0;
		result[9] = 0;
		result[10] = 1;
		result[11] = 0;
		
		result[12] = 0;
		result[13] = 0;
		result[14] = 0;
		result[15] = 1;
		
		return result;
	}
	
	public static RealMatrix getIdentityRealMatrix()
	{
		double result[][] = new double[4][4];
		
		result[0][0] = 1;
		result[0][1] = 0;
		result[0][2] = 0;
		result[0][3] = 0;
		
		result[1][0] = 0;
		result[1][1] = 1;
		result[1][2] = 0;
		result[1][3] = 0;
		
		result[2][0] = 0;
		result[2][1] = 0;
		result[2][2] = 1;
		result[2][3] = 0;
		
		result[3][0] = 0;
		result[3][1] = 0;
		result[3][2] = 0;
		result[3][3] = 1;
		
		return new Array2DRowRealMatrix(result);
	}
	
	public static RealMatrix getMatrixFromUpDirectionAndPosition(double[] directionUpVector, double posX, double posY, double posZ)
    {
		Vector3D vecDir = new Vector3D(directionUpVector);
		
		Vector3D vecZAxis = vecDir.crossProduct(kRightVector);
		vecZAxis = vecZAxis.normalize();
        
		Vector3D vecXAxis = vecDir.crossProduct(vecZAxis);
		vecXAxis = vecXAxis.normalize();

		return new Array2DRowRealMatrix(new double[][]{
				{vecXAxis.getX(),vecXAxis.getY(),vecXAxis.getZ(),posX},
				{vecDir.getX(),vecDir.getY(),vecDir.getZ(),posY},
				{vecZAxis.getX(),vecZAxis.getY(),vecZAxis.getZ(),posZ},
				{0,0,0,1}
				}
		);
		
    }
}

package de.grovie.data;

import org.apache.commons.math3.linear.RealMatrix;

public class GvPack
{
	public int lType;
	public float lValue;
	public RealMatrix lMatrix;
	
	public GvPack(RealMatrix matrix, int type, float value)
	{
		lMatrix = matrix;
		lType = type;
		lValue = value;
	}
}


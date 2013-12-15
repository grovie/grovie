package de.grovie.engine.renderer.device;

public class GvCamera {

	public float lPosition[];
	public float lUp[];
	public float lCenter[];
	
	//temporary storage of orientation for use during interface with i/o devices, e.g. mouse events.
//	public float lPositionTemp[];
//	public float lUpTemp[];
//	public float lCenterTemp[];
	
	public GvCamera()
	{
		lPosition = new float[]{0,0,5.0f};
		lUp = new float[]{0,1,0};
		lCenter = new float[]{0,0,-1.0f};
		
//		lPositionTemp = new float[3];
//		lUpTemp = new float[3];
//		lCenterTemp = new float[3];
	}
	
	public void rotateBegin()
	{
//		lPositionTemp[0] = lPosition[0];
//		lPositionTemp[1] = lPosition[1];
//		lPositionTemp[2] = lPosition[2];
//		
//		lUpTemp[0] = lUp[0];
//		lUpTemp[1] = lUp[1];
//		lUpTemp[2] = lUp[2];
//		
//		lCenterTemp[0] = lCenter[0];
//		lCenterTemp[1] = lCenter[1];
//		lCenterTemp[2] = lCenter[2];
	}
	
	public void rotateEnd()
	{
		
	}
}

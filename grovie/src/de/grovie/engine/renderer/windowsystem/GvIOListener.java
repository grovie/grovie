package de.grovie.engine.renderer.windowsystem;

import org.apache.commons.math3.complex.Quaternion;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.GvRendererStateMachine;
import de.grovie.engine.renderer.device.GvCamera;


/**
 * This class manages input from i/o devices 
 * e.g. mouse, keyboard through the windowing system
 * 
 * @author yong
 *
 */
public class GvIOListener {

	//mouse event constants
	public static final int MOUSE_BUTTON_LEFT 	= 1;
	public static final int MOUSE_BUTTON_MIDDLE = 2;
	public static final int MOUSE_BUTTON_RIGHT 	= 3;
	public static final float MOUSE_SENSITIVITY_ROTATION = 50.0f;
	public static final float MOUSE_SENSITIVITY_TRANSLATION = 10.0f;
	public static final float MOUSE_SENSITIVITY_FORTH_BACK = 5.0f;

	//mouse event variables
	int lMousePressX;
	int lMousePressY;

	//temporary horizontal axis (pre-computed on mouse press for usage during mouse drag)
	Vector3D axisHorizontal;

	//camera orientation on mouse press
	GvCamera lCameraTemp;

	// reference to the renderer
	GvRenderer lRenderer;

	/**
	 * Default constructor
	 */
	public GvIOListener()
	{
		lCameraTemp = new GvCamera();
	}

	public GvRenderer getRenderer() {
		return lRenderer;
	}

	public void setRenderer(GvRenderer renderer) {
		this.lRenderer = renderer;
	}

	/**
	 * Handler method for mouse wheel move
	 * @param scrollAmount
	 */
	public void mouseWheelMovedGv(int wheelRotation)
	{
		GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
		if(stateMachine.setState(GvRendererStateMachine.RendererState.CAMERA_MOVE_FORTH_BACK))
		{
			float dist = wheelRotation/MOUSE_SENSITIVITY_FORTH_BACK;

			rememberCameraOrientation();
			
			stateMachine.cameraSetPosition(
					lCameraTemp.lPosition[0]
							+(dist*(float)lCameraTemp.lView[0]),
							lCameraTemp.lPosition[1]
									+(dist*(float)lCameraTemp.lView[1]),
									lCameraTemp.lPosition[2]
											+(dist*(float)lCameraTemp.lView[2])
					);
			
			stateMachine.setState(GvRendererStateMachine.RendererState.IDLE);

			//redraw to visualize rotation
			lRenderer.redraw();
			
			
		}
	}

	/**
	 * Handler method for mouse press event
	 * @param button
	 * @param x
	 * @param y
	 */
	public void mousePressedGv(int button, int x, int y) {

		if(button == MOUSE_BUTTON_RIGHT)
		{
			if(lRenderer.getRendererStateMachine().setState(
					GvRendererStateMachine.RendererState.CAMERA_ROTATION))
			{
				this.rememberMouseLocation(x,y); //remember mouse press location
				this.rememberCameraOrientation(); //remember camera orientation
			}
		}
		else if(button == MOUSE_BUTTON_MIDDLE)
		{
			if(lRenderer.getRendererStateMachine().setState(
					GvRendererStateMachine.RendererState.CAMERA_TRANSLATION))
			{
				this.rememberMouseLocation(x,y); //remember mouse press location
				this.rememberCameraOrientation(); //remember camera orientation
			}
		}
	}

	/**
	 * Handler method for mouse release event
	 * @param button
	 * @param x
	 * @param y
	 */
	public void mouseReleasedGv(int button, int x, int y)
	{
		if(button == MOUSE_BUTTON_RIGHT)
		{
			GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
			if(stateMachine.getState() == GvRendererStateMachine.RendererState.CAMERA_ROTATION)
			{
				stateMachine.setState(GvRendererStateMachine.RendererState.IDLE);
			}
		}
		else if(button == MOUSE_BUTTON_MIDDLE)
		{
			GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
			if(stateMachine.getState() == GvRendererStateMachine.RendererState.CAMERA_TRANSLATION)
			{
				stateMachine.setState(GvRendererStateMachine.RendererState.IDLE);
			}
		}
	}

	/**
	 * Handler method for mouse drag event
	 * @param x
	 * @param y
	 */
	public void mouseDraggedGv(int x, int y)
	{
		GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
		if(stateMachine.getState() == GvRendererStateMachine.RendererState.CAMERA_ROTATION)
		{
			//translate mouse motion to angles of rotation
			double angleX = (lMousePressX - x)/MOUSE_SENSITIVITY_ROTATION; //rotate along vertical axis
			double angleY = (lMousePressY - y)/MOUSE_SENSITIVITY_ROTATION; //rotate along horizontal axis

			//rotation along horizontal axis (looking up or down)
			Quaternion cameraViewTemp = GvCamera.rotateCameraView(
					lCameraTemp.lView, //original view vector 
					angleY, 		//rotation angle
					axisHorizontal.getX(), axisHorizontal.getY(), axisHorizontal.getZ()); //horizontal rotation axis
			Quaternion cameraUpTemp = GvCamera.rotateCameraView(
					lCameraTemp.lUp, //original up vector 
					angleY, 		//rotation angle
					axisHorizontal.getX(), axisHorizontal.getY(), axisHorizontal.getZ()); //horizontal rotation axis
			
			//rotation along vertical axis (looking left or right)
			Quaternion cameraViewNew = GvCamera.rotateCameraView(
					cameraViewTemp.getVectorPart(),
					angleX,
					0, 1, 0);
			cameraViewNew = cameraViewNew.normalize();	
			double[] cameraViewNewArray = cameraViewNew.getVectorPart();
			
			Quaternion cameraUpNew = GvCamera.rotateCameraView(
					cameraUpTemp.getVectorPart(),
					angleX,
					0, 1, 0);
			cameraUpNew = cameraUpNew.normalize();
			double[] cameraUpNewArray = cameraUpNew.getVectorPart();

			//set new camera view vector
			stateMachine.cameraSetView((float)cameraViewNewArray[0],
					(float)cameraViewNewArray[1],
					(float)cameraViewNewArray[2]);
			//set new camera up vector
			stateMachine.cameraSetUp((float)cameraUpNewArray[0],
					(float)cameraUpNewArray[1],
					(float)cameraUpNewArray[2]);
			
			this.rememberMouseLocation(x,y); //remember mouse press location
			this.rememberCameraOrientation(); //remember camera orientation

			//redraw to visualize rotation
			lRenderer.redraw();

		}
		else if(stateMachine.getState() == GvRendererStateMachine.RendererState.CAMERA_TRANSLATION)
		{
			//translate mouse motion to translation distance
			float distX = (x-lMousePressX)/MOUSE_SENSITIVITY_TRANSLATION;
			float distY = (y-lMousePressY)/MOUSE_SENSITIVITY_TRANSLATION;

			stateMachine.cameraSetPosition(
					lCameraTemp.lPosition[0]
							+(distX*(float)axisHorizontal.getX())
							-(distY*(float)lCameraTemp.lUp[0]),
							lCameraTemp.lPosition[1]
									+(distX*(float)axisHorizontal.getY())
									-(distY*(float)lCameraTemp.lUp[1]),
									lCameraTemp.lPosition[2]
											+(distX*(float)axisHorizontal.getZ())
											-(distY*(float)lCameraTemp.lUp[2])
					);

			//redraw to visualize rotation
			lRenderer.redraw();
		}
	}

	/**
	 * Store mouse coordinates in this GvIOListener instance
	 * @param x
	 * @param y
	 */
	private void rememberMouseLocation(int x, int y)
	{
		this.lMousePressX = x;
		this.lMousePressY = y;
	}

	/**
	 * Store camera orientation in this GvIOListener instance
	 */
	private void rememberCameraOrientation()
	{
		lRenderer.getRendererStateMachine().getCamera(lCameraTemp);
		//pre-compute and store horizontal axis
		axisHorizontal = Vector3D.crossProduct(
				new Vector3D(lCameraTemp.lView[0],lCameraTemp.lView[1],lCameraTemp.lView[2]), 
				new Vector3D(lCameraTemp.lUp[0],lCameraTemp.lUp[1],lCameraTemp.lUp[2])
				);
		axisHorizontal = axisHorizontal.normalize();
	}


}

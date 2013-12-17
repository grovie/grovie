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
	public static final int MOUSE_BUTTON_LEFT = 1;
	public static final int MOUSE_BUTTON_MIDDLE = 2;
	public static final int MOUSE_BUTTON_RIGHT = 3;
	public static final float MOUSE_SENSITIVITY = 30.0f;

	//mouse event variables
	int lMousePressX;
	int lMousePressY;

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
	 * Handler method for mouse press event
	 * @param button
	 * @param x
	 * @param y
	 */
	public void mousePressedGv(int button, int x, int y) {

		if(button == MOUSE_BUTTON_RIGHT)
		{
			GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
			if(stateMachine.setState(
					GvRendererStateMachine.RendererState.CAMERA_ROTATION
					))
			{
				//remember mouse press location
				this.lMousePressX = x;
				this.lMousePressY = y;
				//remember camera orientation
				stateMachine.getCamera(lCameraTemp);
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
				stateMachine.setState(
						GvRendererStateMachine.RendererState.IDLE
						);
			}
			//System.out.println("mouse release: (" + x + ", " + y +")"); //FOR DEBUG
		}


	}

	/**
	 * Handler method for mouse drag event
	 * @param x
	 * @param y
	 */
	public void mouseDraggedGv(int button, int x, int y)
	{
		GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
		if(stateMachine.getState() == GvRendererStateMachine.RendererState.CAMERA_ROTATION)
		{
			//translate mouse motion to angles of rotation
			double angleX = (lMousePressX - x)/MOUSE_SENSITIVITY; //rotate along vertical axis
			double angleY = (lMousePressY - y)/MOUSE_SENSITIVITY; //rotate along horizontal axis

			//horizontal rotation axis
			Vector3D axis = Vector3D.crossProduct(
					new Vector3D(lCameraTemp.lView[0],lCameraTemp.lView[1],lCameraTemp.lView[2]), 
					new Vector3D(lCameraTemp.lUp[0],lCameraTemp.lUp[1],lCameraTemp.lUp[2])
					);
			axis = axis.normalize();

			//rotation along horizontal axis (looking up or down)
			Quaternion cameraViewTemp = GvCamera.rotateCameraView(
					lCameraTemp.lView, //original view vector 
					angleY, 		//rotation angle
					axis.getX(), axis.getY(), axis.getZ()); //horizontal rotation axis

			//rotation along vertical axis (looking left or right)
			Quaternion cameraViewNew = GvCamera.rotateCameraView(
					cameraViewTemp.getVectorPart(),
					angleX,
					0, 1, 0);
			double[] cameraViewNewArray = cameraViewNew.getVectorPart();

			//set new camera view vector
			stateMachine.cameraRotate((float)cameraViewNewArray[0],
					(float)cameraViewNewArray[1],
					(float)cameraViewNewArray[2]);

			//redraw to visualize rotation
			lRenderer.redraw();

		}
		//System.out.println("mouse dragged: (" + x + ", " + y +")"); //FOR DEBUG

	}
}

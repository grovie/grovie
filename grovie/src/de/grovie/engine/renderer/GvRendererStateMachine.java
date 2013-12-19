package de.grovie.engine.renderer;

import de.grovie.engine.renderer.device.GvCamera;

/**
 * This class is a state machine providing access to components in the renderer that can be modified.
 * 
 * @author yong
 *
 */
public class GvRendererStateMachine {

	public enum RendererState
	{
		IDLE,
		CAMERA_ROTATION,
		CAMERA_TRANSLATION,
		CAMERA_MOVE_FORTH_BACK,
		CAMERA_FOV_CHANGE,
		CAMERA_ASPECT_CHANGE,
		CAMERA_NEAR_CHANGE,
		CAMERA_FAR_CHANGE,
		SCREEN_DIMENSIONS_CHANGE;
	}
	
	/** Renderer state is a state machine. 
	 * At one moment in time, it is operating/resting in a single state.
	 * E.g., camera rotation, etc.
	 */ 
	private RendererState lState;	
	
	/**
	 * Camera instance
	 */
	private GvCamera lCamera;
	
	/**
	 * Screen dimensions
	 */
	private int lScreenWidth;
	private int lScreenHeight;
	
	/**
	 * Default constructor - default state is idle
	 */
	public GvRendererStateMachine()
	{
		lState = RendererState.IDLE; //handler begins in idling state
		lCamera = new GvCamera();
	}
	
	/**
	 * Constructor
	 */
	public GvRendererStateMachine(int screenWidth, int screenHeight)
	{
		lState = RendererState.IDLE; //handler begins in idling state
		lCamera = new GvCamera((float)screenWidth/(float)screenHeight);
		lScreenWidth = screenWidth;
		lScreenHeight= screenHeight;
	}
	
	/**
	 * Change state of state machine
	 * @param state
	 * @return true if state was set, false otherwise
	 */
	public boolean setState(RendererState state)
	{
		if(lState == RendererState.IDLE)
		{
			lState = state;
			return true;
		}
		else
		{
			if(state == RendererState.IDLE)
			{
				lState = RendererState.IDLE;
				return true;
			}
		}
		return false;
	}
	
	public RendererState getState()
	{
		return lState;
	}
	
	public void cameraSetView(float x, float y, float z)
	{
		if(lState == RendererState.CAMERA_ROTATION)
		{
			lCamera.setView(x, y, z);
		}
	}
	
	public void cameraSetPosition(float x, float y, float z)
	{
		if((lState == RendererState.CAMERA_TRANSLATION)||
			(lState == RendererState.CAMERA_MOVE_FORTH_BACK)
				)
		{
			lCamera.setPosition(x, y, z);
		}
	}
	
	public void cameraSetUp(float x, float y, float z)
	{
		if(lState == RendererState.CAMERA_ROTATION)
		{
			lCamera.setUp(x, y, z);
		}
	}
	
	public void getCamera(GvCamera anotherCamera)
	{
		lCamera.copyCamera(anotherCamera);
	}
	
	public void cameraSetAspect(float aspect)
	{
		if(lState == RendererState.CAMERA_ASPECT_CHANGE)
		{
			lCamera.setAspect(aspect);
		}
	}
	
	public void cameraSetNear(float near)
	{
		if(lState == RendererState.CAMERA_NEAR_CHANGE)
		{
			lCamera.setNear(near);
		}
	}
	
	public void cameraSetFar(float far)
	{
		if(lState == RendererState.CAMERA_FAR_CHANGE)
		{
			lCamera.setFar(far);
		}
	}
	
	public void cameraSetFov(float fov)
	{
		if(lState == RendererState.CAMERA_FOV_CHANGE)
		{
			lCamera.setFov(fov);
		}
	}
	
	public void screenSetDimensions(int width, int height)
	{
		if(lState == RendererState.SCREEN_DIMENSIONS_CHANGE)
		{
			lScreenWidth = width;
			lScreenHeight = height;
		}
	}
	
	public int getScreenWidth()
	{
		return lScreenWidth;
	}
	
	public int getScreenHeight()
	{
		return lScreenHeight;
	}
}

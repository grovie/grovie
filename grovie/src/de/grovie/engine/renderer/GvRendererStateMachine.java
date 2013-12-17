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
		CAMERA_MOVE_FORTH_BACK;
	}
	
	/** Renderer state is a state machine. 
	 * At one moment in time, it is operating/resting in a single state.
	 * E.g., camera rotation, etc.
	 */ 
	private RendererState lState;	
	
	/**
	 * Camera instance
	 */
	GvCamera lCamera;
	
	/**
	 * Default constructor - default state is idle
	 */
	public GvRendererStateMachine()
	{
		lState = RendererState.IDLE; //handler begins in idling state
		lCamera = new GvCamera();
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
		else if(lState == RendererState.CAMERA_ROTATION)
		{
			if(state == RendererState.IDLE)
			{
				lState = RendererState.IDLE;
				return true;
			}
		}
		else if(lState == RendererState.CAMERA_TRANSLATION)
		{
			if(state == RendererState.IDLE)
			{
				lState = RendererState.IDLE;
				return true;
			}
		}
		else if(lState == RendererState.CAMERA_MOVE_FORTH_BACK)
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
		anotherCamera.lPosition[0] = lCamera.lPosition[0];
		anotherCamera.lPosition[1] = lCamera.lPosition[1];
		anotherCamera.lPosition[2] = lCamera.lPosition[2];
		
		anotherCamera.lUp[0] = lCamera.lUp[0];
		anotherCamera.lUp[1] = lCamera.lUp[1];
		anotherCamera.lUp[2] = lCamera.lUp[2];
		
		anotherCamera.lView[0] = lCamera.lView[0];
		anotherCamera.lView[1] = lCamera.lView[1];
		anotherCamera.lView[2] = lCamera.lView[2];
	}
}

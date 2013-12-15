package de.grovie.engine.renderer;

import de.grovie.engine.renderer.device.GvCamera;

public class GvRendererStateMachine {

	public enum RendererState
	{
		IDLE,
		CAMERA_ROTATION,
		CAMERA_TRANSLATION
	}
	
	// Renderer state is a state machine. 
	// At one moment in time, it is operating/resting in a single state.
	// E.g., camera rotation, etc.
	private RendererState lState;	
	
	// Camera
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
	 */
	public void setState(RendererState state)
	{
		if(lState == RendererState.IDLE)
		{
			lState = state;
		}
		else if(lState == RendererState.CAMERA_ROTATION)
		{
			if(state == RendererState.IDLE)
				lState = RendererState.IDLE;
			else
				return;
		}
	}
	
	public void cameraRotationBegin()
	{
		if(lState == RendererState.CAMERA_ROTATION)
		{
			
		}
	}
	
	public void cameraRotationEnd()
	{
		if(lState == RendererState.CAMERA_ROTATION)
		{
			
		}
	}
	
	public void cameraRotate()
	{
		if(lState == RendererState.CAMERA_ROTATION)
		{
			
		}
	}
}

package de.grovie.engine.renderer;

import de.grovie.engine.renderer.device.GvCamera;
import de.grovie.engine.renderer.device.GvLight;

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
	 * Lights
	 */
	private static int DEFAULT_LIGHT_COUNT = 3;
	private static float[] DEFAULT_LIGHT_0_POS = new float[]{GvLight.DEFAULT_POSITION[0],
		GvLight.DEFAULT_POSITION[1],
		GvLight.DEFAULT_POSITION[2]};
	private static float[] DEFAULT_LIGHT_1_POS = new float[]{-0.57735026919f,0.57735026919f,0.57735026919f};
	private static float[] DEFAULT_LIGHT_2_POS = new float[]{0,0.70710678118f,-0.70710678118f};
	private GvLight[] lLights;
	
	/**
	 * Default constructor - hidden
	 */
	private GvRendererStateMachine()
	{
	}
	
	/**
	 * Constructor
	 */
	public GvRendererStateMachine(int screenWidth, int screenHeight)
	{
		lCamera = new GvCamera((float)screenWidth/(float)screenHeight);
		lScreenWidth = screenWidth;
		lScreenHeight= screenHeight;
			
		lState = RendererState.IDLE; //handler begins in idling state
		lLights = new GvLight[DEFAULT_LIGHT_COUNT];
		lLights[0] = new GvLight(DEFAULT_LIGHT_0_POS[0],
				DEFAULT_LIGHT_0_POS[1],
				DEFAULT_LIGHT_0_POS[2]);
		lLights[1] = new GvLight(DEFAULT_LIGHT_1_POS[0],
				DEFAULT_LIGHT_1_POS[1],
				DEFAULT_LIGHT_1_POS[2]);
		lLights[2] = new GvLight(DEFAULT_LIGHT_2_POS[0],
				DEFAULT_LIGHT_2_POS[1],
				DEFAULT_LIGHT_2_POS[2]);
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
	
	public void getLight(int index, GvLight anotherLight)
	{
		if((index>=0)&&(lLights.length >=index))
		{
			lLights[index].copyLight(anotherLight);
		}
	}
	
	public int getLightCount()
	{
		return lLights.length;
	}
}

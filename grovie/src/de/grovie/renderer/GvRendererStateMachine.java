package de.grovie.renderer;



/**
 * This class is a state machine providing access to entities
 * in the renderer that can be modified.
 * 
 * @author yong
 *
 */
public class GvRendererStateMachine {

	public enum GvRendererState
	{
		IDLE,
		CAMERA_ROTATION,
		CAMERA_TRANSLATION,
		CAMERA_MOVE_FORTH_BACK,
		CAMERA_FOV_CHANGE,
		CAMERA_ASPECT_CHANGE,
		CAMERA_NEAR_CHANGE,
		CAMERA_FAR_CHANGE,
		SCREEN_DIMENSIONS_CHANGE,
		OVERLAY_ON_CHANGE,
		RENDER_STATE_CHANGE;
	}
	
	/** Renderer state is a state machine. 
	 * At one moment in time, it is operating/resting in a single state.
	 * E.g., camera rotation, etc.
	 */ 
	private GvRendererState lState;	
	
	/**
	 * Camera instance
	 */
	private GvCamera lCamera;
	
	/**
	 * Screen dimensions
	 */
	private GvScreen lScreen;
	
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
	 * Overlay 2D
	 */
	private boolean lOverlayOn;
	
	/**
	 * Constructor
	 */
	public GvRendererStateMachine(int screenWidth, int screenHeight)
	{
		lCamera = new GvCamera((float)screenWidth/(float)screenHeight);
		lScreen = new GvScreen(screenWidth, screenHeight);
			
		lState = GvRendererState.IDLE; //handler begins in idling state
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
		
		lOverlayOn = true;
	}
	
	/**
	 * Change state of state machine
	 * @param state
	 * @return true if state was set, false otherwise
	 */
	public boolean setState(GvRendererState state)
	{
		if(lState == GvRendererState.IDLE)
		{
			lState = state;
			return true;
		}
		else
		{
			if(state == GvRendererState.IDLE)
			{
				lState = GvRendererState.IDLE;
				return true;
			}
		}
		return false;
	}
	
	public GvRendererState getState()
	{
		return lState;
	}
	
	public void cameraSetView(float x, float y, float z)
	{
		if(lState == GvRendererState.CAMERA_ROTATION)
		{
			lCamera.setView(x, y, z);
		}
	}
	
	public void cameraSetPosition(float x, float y, float z)
	{
		if((lState == GvRendererState.CAMERA_TRANSLATION)||
			(lState == GvRendererState.CAMERA_MOVE_FORTH_BACK)
				)
		{
			lCamera.setPosition(x, y, z);
		}
	}
	
	public void cameraSetUp(float x, float y, float z)
	{
		if(lState == GvRendererState.CAMERA_ROTATION)
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
		if(lState == GvRendererState.CAMERA_ASPECT_CHANGE)
		{
			lCamera.setAspect(aspect);
		}
	}
	
	public void cameraSetNear(float near)
	{
		if(lState == GvRendererState.CAMERA_NEAR_CHANGE)
		{
			lCamera.setNear(near);
		}
	}
	
	public void cameraSetFar(float far)
	{
		if(lState == GvRendererState.CAMERA_FAR_CHANGE)
		{
			lCamera.setFar(far);
		}
	}
	
	public void cameraSetFov(float fov)
	{
		if(lState == GvRendererState.CAMERA_FOV_CHANGE)
		{
			lCamera.setFov(fov);
		}
	}
	
	public void screenSetDimensions(int width, int height)
	{
		if(lState == GvRendererState.SCREEN_DIMENSIONS_CHANGE)
		{
			lScreen.setScreenWidth(width);
			lScreen.setScreenHeight(height);
		}
	}
	
	public void overlayOnToggle()
	{
		if(lState == GvRendererState.OVERLAY_ON_CHANGE)
		{
			lOverlayOn = !lOverlayOn;
		}
	}
	
	public int getScreenWidth()
	{
		return lScreen.getScreenWidth();
	}
	
	public int getScreenHeight()
	{
		return lScreen.getScreenHeight();
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
	
	public boolean getOverlayOn()
	{
		return lOverlayOn;
	}
}

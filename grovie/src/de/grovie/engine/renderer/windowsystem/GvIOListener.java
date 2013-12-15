package de.grovie.engine.renderer.windowsystem;


/**
 * This class manages input from i/o devices - mouse, keyboard, etc.
 * It provides i/o functionality in response to events from i/o devices, 
 * independent from the implementing windowing system.
 * 
 * @author yong
 *
 */
public class GvIOListener {

	//i/o handler states
	public static int STATE_IDLE = 1;
	public static int STATE_CAMERA_ROTATION = 2;
	public static int STATE_CAMERA_TRANSLATION = 3;
	
	//mouse event constants
	public static int MOUSE_BUTTON_LEFT = 1;
	public static int MOUSE_BUTTON_MIDDLE = 2;
	public static int MOUSE_BUTTON_RIGHT = 3;
	
	// reference to the drawing canvas
	GvCanvas lCanvas;
	
	// io listener is a state machine. 
	// At one moment in time, it is operating/resting in a single state.
	// E.g., camera rotation, etc.
	private int lState;	
	
	/**
	 * Default constructor
	 */
	public GvIOListener()
	{
		lState = STATE_IDLE; //handler begins in idling state
	}

	public GvCanvas getCanvas() {
		return lCanvas;
	}

	public void setCanvas(GvCanvas canvas) {
		this.lCanvas = canvas;
	}
	
	/**
	 * Handler method for mouse press event
	 * @param button
	 * @param x
	 * @param y
	 */
	public void mousePressedGv(int button, int x, int y) {
		if(lState != STATE_IDLE)
			return;
		
		if(button == MOUSE_BUTTON_RIGHT)
		{
			lState = STATE_CAMERA_ROTATION;
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
		if((lState == STATE_CAMERA_ROTATION) && (button == MOUSE_BUTTON_RIGHT))
		{
			lState = STATE_IDLE;
		}
	}
	
	/**
	 * Handler method for mouse drag event
	 * @param x
	 * @param y
	 */
	public void mouseDraggedGv(int x, int y)
	{
		if(lState == STATE_CAMERA_ROTATION)
		{
			
		}
	}
}

package de.grovie.engine.renderer.windowsystem;

import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.GvRendererStateMachine;


/**
 * This class manages input from i/o devices 
 * e.g. mouse, keyboard through the windowing system
 * 
 * @author yong
 *
 */
public class GvIOListener {

	//mouse event constants
	public static int MOUSE_BUTTON_LEFT = 1;
	public static int MOUSE_BUTTON_MIDDLE = 2;
	public static int MOUSE_BUTTON_RIGHT = 3;
	
	// reference to the renderer
	GvRenderer lRenderer;
	
	/**
	 * Default constructor
	 */
	public GvIOListener()
	{
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
			stateMachine.setState(
					GvRendererStateMachine.RendererState.CAMERA_ROTATION
				);
			stateMachine.cameraRotationBegin();
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
			stateMachine.cameraRotationEnd();
			stateMachine.setState(
					GvRendererStateMachine.RendererState.IDLE
				);
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
		if(button == MOUSE_BUTTON_RIGHT)
		{
			GvRendererStateMachine stateMachine = lRenderer.getRendererStateMachine();
			stateMachine.cameraRotate();
			//System.out.println("mouse dragged: (" + x + ", " + y +")"); //FOR DEBUG
		}
	}
}

package de.grovie.sandbox;

import de.grovie.engine.GvEngine;
import de.grovie.engine.GvEngine.GvEngineMode;
import de.grovie.engine.renderer.GL2.GvRendererGL2;
import de.grovie.engine.renderer.windowsystem.AWT.GvWindowSystemAWT;

public class GvSandbox {

	public static final int kWindowWidth = 640;
	public static final int kWindowHeight = 480;
	
	public static void main(String[] args)
	{
		//start splash screen
		
		//get absolute path to database location
		
		//create GroViE vis. engine
		GvEngine engine = GvEngine.getInstance(); //uses embedded db by default
		
		//create windowing system - Java AWT
		GvWindowSystemAWT windowSystem = new GvWindowSystemAWT();
		
		//create renderer to use
		GvRendererGL2 gvRenderer = new GvRendererGL2(
				windowSystem,
				"GroViE Sandbox",
				kWindowWidth,
				kWindowHeight);
		
		//start the visualization engine
		if(engine.getMode() == GvEngineMode.EMBEDDED)
		{
			engine.start("C:\\Users\\yong\\db", gvRenderer); //TODO: replace with path obtained from pop up dialog
		}
		
		//obtain graph db from engine. 
		//if multi thread, create worker threads. give threads reference to db.
		
		//begin scene modifications on db (changes should be seen on the rendering window)
		
		//prevent sandbox application from closing until <Return> key is pressed
		/*try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}

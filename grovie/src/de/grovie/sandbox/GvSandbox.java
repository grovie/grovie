package de.grovie.sandbox;

import de.grovie.engine.GvEngine;
import de.grovie.engine.GvEngine.GvEngineMode;

public class GvSandbox {

	public static final int kWindowWidth = 640;
	public static final int kWindowHeight = 480;
	
	public static void main(String[] args)
	{
		//get absolute path to database location
		
		//create GroViE vis. engine
		GvEngine engine = GvEngine.getInstance(
				GvEngine.GvWindowSystemLibrary.AWT_OPEN_GL,
				GvEngine.GvGraphicsAPI.OPEN_GL_2_0,
				kWindowWidth,
				kWindowHeight,
				"GroViE Sandbox"); //uses embedded db by default
		
		//start the visualization engine
		if(engine.getMode() == GvEngineMode.EMBEDDED)
		{
			engine.start("C:\\Users\\yong\\db"); //TODO: replace with path obtained from pop up dialog
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

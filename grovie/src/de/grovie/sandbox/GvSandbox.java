package de.grovie.sandbox;

import de.grovie.engine.GvEngine;
import de.grovie.engine.GvEngine.GvEngineMode;
import de.grovie.engine.renderer.AWTGL.GvRendererAWTGL;

public class GvSandbox {

	public static void main(String[] args)
	{
		//start splash screen
		
		//get absolute path to database location
		
		//create GroViE vis. engine
		GvEngine gvEngine = GvEngine.getInstance(); //uses embedded db by default
		
		//create renderer to use
		GvRendererAWTGL gvRenderer = new GvRendererAWTGL(gvEngine);
		
		//start the visualization engine
		if(gvEngine.getMode() == GvEngineMode.EMBEDDED)
		{
			gvEngine.start("C:\\Users\\yong\\db", gvRenderer); //TODO: replace with path obtained from pop up dialog
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

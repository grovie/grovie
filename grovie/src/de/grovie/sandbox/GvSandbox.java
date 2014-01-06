package de.grovie.sandbox;

import de.grovie.engine.GvEngine;
import de.grovie.engine.GvEngine.GvEngineMode;
import de.grovie.exception.GvExDbUnrecognizedImpl;

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
			try {
				engine.start("C:\\Users\\yong\\db");
				//engine.start("/Users/yongzhiong/Desktop/testdb");
			} catch (GvExDbUnrecognizedImpl e) {
				
				e.printStackTrace();
			} //TODO: replace with path obtained from pop up dialog
		}
		
		for(int i=1; i< 1000; ++i)
		{
			engine.simulationStep(i);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
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

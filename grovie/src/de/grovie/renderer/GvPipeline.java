package de.grovie.renderer;

import java.util.ArrayList;

public class GvPipeline {

	private ArrayList<GvPass> lPasses; //rendering passes
	
	public GvPipeline()
	{
		this.lPasses = new ArrayList<GvPass>();
	}
	
	public void execute()
	{
		for(int i=0; i<lPasses.size(); ++i)
		{
			GvPass pass = lPasses.get(i);
			pass.start();
			pass.execute();
			pass.stop();
		}
	}
	
	public void addPass(GvPass pass)
	{
		lPasses.add(pass);
	}
}

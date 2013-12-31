package de.grovie.renderer;

import java.util.ArrayList;

public abstract class GvPipeline {

	protected GvRenderer lRenderer;		//reference to renderer
	protected ArrayList<GvPass> lPasses; 	//rendering passes
	
	public GvPipeline(GvRenderer renderer)
	{
		this.lRenderer = renderer;
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
	
	public abstract void reshape(int x, int y, int width, int height);
}

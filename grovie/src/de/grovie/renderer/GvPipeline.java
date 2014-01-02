package de.grovie.renderer;

import java.util.ArrayList;

import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererPassPrimitiveTypeUnknown;

public abstract class GvPipeline {

	protected GvRenderer lRenderer;		//reference to renderer
	protected ArrayList<GvPass> lPasses; 	//rendering passes
	
	public GvPipeline(GvRenderer renderer)
	{
		this.lRenderer = renderer;
		this.lPasses = new ArrayList<GvPass>();
	}
	
	public void execute() throws GvExRendererDrawGroupRetrieval, GvExRendererPassPrimitiveTypeUnknown
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
	
	public GvPass getPass()
	{
		return lPasses.get(0);
	}
	
	public abstract void reshape(int x, int y, int width, int height);
}

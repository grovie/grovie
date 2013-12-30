package de.grovie.renderer;

public abstract class GvIllustrator{

	protected GvRenderer lRenderer;	//reference to owner(GvRenderer)
	protected GvPipeline lPipeline;	//3D pipeline
	
	protected long lDrawStart;	//start time before drawing a frame
	protected double lFrameTime;//time taken to draw a frame
	
	public GvIllustrator(GvRenderer renderer)
	{
		lRenderer = renderer;
		lDrawStart=0;
		lFrameTime=0;
	}
	
	public void display()
	{
		//Record start time
		lDrawStart = System.nanoTime();
		
		//draw 3d and 2d-overlay
		display3D();
		display2DOverlay();
		displayEnd(); //finishing calls
		
		//Record end time
		lFrameTime = (double)((System.nanoTime()-lDrawStart) / 1000000000.0);
	}
	
	public void display3D()
	{
		lPipeline.execute();
	}
	
	public abstract void init();	//initialize rendering, init 3D-pipeline
	public abstract void reshape(int x, int y, int width, int height);
	public abstract void display2DOverlay();
	public abstract void displayEnd();
	public abstract void dispose();
	
}

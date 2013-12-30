package de.grovie.renderer;

public abstract class GvIllustrator{

	protected GvRenderer lRenderer;
	
	protected long lDrawStart;	//start time before drawing a frame
	protected double lFrameTime;//time taken to draw a frame
	
	public GvIllustrator(GvRenderer renderer)
	{
		lRenderer = renderer;
		lDrawStart=0;
		lFrameTime=0;
	}
	
	public abstract void reshape(int x, int y, int width, int height);
	public abstract void init();
	public abstract void display();
	public abstract void dispose();
}

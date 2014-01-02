package de.grovie.renderer;

import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererPassPrimitiveTypeUnknown;
import de.grovie.exception.GvExRendererPassShaderResource;

public abstract class GvIllustrator{

	protected GvRenderer lRenderer;	//reference to owner(GvRenderer)
	protected GvPipeline lPipeline;	//3D pipeline
	
	protected long lDrawStart;			//start time before drawing a frame
	protected double lFrameTime;		//time taken to draw a frame
	protected double lFrameTimeAccum;	//accumulated frame timings (used to compute average)
	protected int lFrameCountAccum;		//accumulated frame count (used to compute average)
	protected long lVertexCount;		//number of vertices rendered
	
	public GvIllustrator(GvRenderer renderer)
	{
		lRenderer = renderer;
		lDrawStart=0;
		lFrameTime=0;
		lFrameTimeAccum = 0;
		lFrameCountAccum=0;
		lVertexCount=0;
	}
	
	public void display()
	{
		//Record start time
		lDrawStart = System.nanoTime();
		
		//Inter-thread message queue handling
		processMessages();
		
		//draw 3d and 2d-overlay
		try{
			display3D();
		}
		catch(GvExRendererDrawGroupRetrieval e)
		{
			System.out.println(e.getMessage());
		} catch (GvExRendererPassPrimitiveTypeUnknown e) {
			System.out.println(e.getMessage());
		}
		display2DOverlay();
		displayEnd(); //finishing calls
		
		//Record end time
		lFrameTimeAccum += (double)((System.nanoTime()-lDrawStart) / 1000000000.0);
		lFrameCountAccum ++;
		
		if(lFrameTimeAccum > 2.0)
		{
			lFrameTime = lFrameTimeAccum/(double)lFrameCountAccum;
			lFrameTimeAccum = 0;
			lFrameCountAccum = 0;
		}
	}
	
	public void display3D() throws GvExRendererDrawGroupRetrieval, GvExRendererPassPrimitiveTypeUnknown
	{
		lPipeline.execute();
	}
	
	public void setVertexCount(long vertexCount)
	{
		lVertexCount = vertexCount;
	}
	
	public long getVertexCount()
	{
		return lVertexCount;
	}
	
	public abstract void init() throws GvExRendererPassShaderResource;	//initialize rendering, init 3D-pipeline
	public abstract void reshape(int x, int y, int width, int height);
	public abstract void display2DOverlay();
	public abstract void displayEnd();
	public abstract void dispose();
	public abstract void processMessages();
}

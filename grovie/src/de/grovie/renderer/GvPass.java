package de.grovie.renderer;

import de.grovie.exception.GvExRendererPassShaderResource;

/**
 * An abstract representation of a rendering pass.
 * 
 * @author yong
 *
 */
public abstract class GvPass {
	
	protected GvRenderer lRenderer;
	
	public GvPass(GvRenderer renderer)
	{
		this.lRenderer = renderer;
	}
	
	//rendering pass initialization
	public abstract void init() throws GvExRendererPassShaderResource;
	
	//rendering pass viewport re-shaping
	public abstract void reshape(int x, int y, int width, int height);
	
	//pre-execution procedures
	public abstract void start();
	
	//rendering pass execution
	public abstract void execute();
	
	//post-execution procedures
	public abstract void stop();	
}

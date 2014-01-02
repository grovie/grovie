package de.grovie.renderer;

import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererPassShaderResource;

/**
 * An abstract representation of a rendering pass.
 * A rendering pass signifies the sending of data into
 * the 3D-to-2D (i.e. 3D geometry to screen color) conversion channel.
 * 
 * @author yong
 *
 */
public abstract class GvPass {
	
	protected GvRenderer lRenderer;						//reference to renderer
	
	public GvPass(GvRenderer renderer)
	{
		lRenderer = renderer;
	}
	
	//rendering pass initialization
	public abstract void init() throws GvExRendererPassShaderResource;
	
	//rendering pass viewport re-shaping
	public abstract void reshape(int x, int y, int width, int height);
	
	//pre-execution procedures
	public abstract void start();
	
	//rendering pass execution
	public abstract void execute() throws GvExRendererDrawGroupRetrieval;
	
	//post-execution procedures
	public abstract void stop();	
}

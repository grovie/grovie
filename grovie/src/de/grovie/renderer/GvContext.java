package de.grovie.renderer;

import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererVertexArray;

/**
 * This class represents a factory for graphics-related entities
 * that cannot be shared between contexts.
 * 
 * @author yong
 *
 */
public abstract class GvContext {

	protected GvRenderer lRenderer;
	
	public GvContext(GvRenderer renderer)
	{
		lRenderer = renderer;
	}
	
	public abstract GvVertexArray createVertexArray() 
			throws GvExRendererVertexArray;
	
	public abstract GvDrawGroup createDrawGroup() 
			throws GvExRendererDrawGroup;
	
	public abstract GvDrawGroup createBufferSet(GvDevice device, GvContext context) 
			throws GvExRendererBufferSet;
}

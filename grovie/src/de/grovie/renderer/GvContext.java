package de.grovie.renderer;

import de.grovie.exception.GvExceptionRendererVertexArray;

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
	
	public abstract GvVertexArray createVertexArray() throws GvExceptionRendererVertexArray;
}

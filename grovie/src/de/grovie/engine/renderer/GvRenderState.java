package de.grovie.engine.renderer;

/**
 * This class represents a group of render states. 
 * @author yong
 *
 */
public class GvRenderState {

	public GvPrimitiveRestart lPrimitiveRestart;
	public GvFacetCulling lFacetCulling;
	public GvRasterizationMode lRasterizationMode;
	public GvScissorTest lScissorTest;
	public GvStencilTest lStencilTest;
	public GvDepthTest lDepthTest;
	public GvDepthRange lDepthRange;
	public GvBlending lBlending;
	public GvColorMask lColorMask;
	public boolean lDepthMask;
}

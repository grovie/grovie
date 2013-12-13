package de.grovie.engine.renderer.renderstate;

/**
 * This class represents a group of render states.
 * In OpenGL, these states are global. In GroViE, these states
 * are not global and are passed into each draw call. The states
 * are, however, grouped and managed by the engine.
 * 
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

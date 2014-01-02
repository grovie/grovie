package de.grovie.renderer.renderstate;

/**
 * This class represents a group of render states.
 * In OpenGL, these states are global. In GroViE, these states
 * are not global and are passed into each draw call. The states
 * are, however, grouped and managed by the engine.
 * 
 * @author yong
 *
 */
public abstract class GvRenderState {

	public GvFaceCulling lFaceCulling;
	public GvRasterizationMode lRasterizationMode;
	public GvScissorTest lScissorTest;
	public GvStencilTest lStencilTest;
	public GvDepthTest lDepthTest;
	public GvLighting lLighting;
	public GvTexture lTexture;
	public boolean lDepthMask;
	
	public GvRenderState()
	{
		lFaceCulling = new GvFaceCulling();
		lRasterizationMode = new GvRasterizationMode();
		lScissorTest = new GvScissorTest();
		lStencilTest = new GvStencilTest();
		lDepthTest = new GvDepthTest();
		lLighting = new GvLighting();
		lTexture = new GvTexture();
		lDepthMask = true;
	}

	public abstract void update(GvRenderState newState, Object context);
}

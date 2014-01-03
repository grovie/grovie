package de.grovie.renderer.renderstate;

/**
 * This class represents a group of render states.
 * In OpenGL, these states are global. In GroViE, these states
 * are not global. The states are, however, grouped and updated by the engine.
 * 
 * @author yong
 *
 */
public abstract class GvRenderState {
	
	public GvDepthTest lDepthTest;
	public GvFaceCulling lFaceCulling;
	public GvLighting lLighting;
	public GvRasterizationMode lRasterizationMode;
	public GvScissorTest lScissorTest;
	public GvStencilTest lStencilTest;
	public GvTexture lTexture;
	public boolean lDepthMask;
	
	public GvRenderState()
	{
		lDepthTest = new GvDepthTest();
		lFaceCulling = new GvFaceCulling();
		lLighting = new GvLighting();
		lRasterizationMode = new GvRasterizationMode();
		lScissorTest = new GvScissorTest();
		lStencilTest = new GvStencilTest();
		lTexture = new GvTexture();
		lDepthMask = false;
	}

	public abstract void update(GvRenderState newState, Object context);
}

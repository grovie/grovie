package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;

import de.grovie.renderer.renderstate.GvDepthTest;
import de.grovie.renderer.renderstate.GvRasterizationMode;
import de.grovie.renderer.renderstate.GvRenderState;

public class GvRenderStateGL2 extends GvRenderState {
	
	@Override
	public void update(GvRenderState newState, Object context) {
		GL2 gl2 = (GL2)context;
		
		if(lFaceCulling.isDifferent(newState.lFaceCulling))
			updateFaceCulling(gl2,newState);
		
		if(lRasterizationMode.isDifferent(newState.lRasterizationMode))
			updateRasterizationMode(gl2,newState);
		
		if(lDepthTest.isDifferent(newState.lDepthTest))
			updateDepthTest(gl2,newState);
		
		if(lLighting.isDifferent(newState.lLighting))
			updateLighting(gl2,newState);
		
		if(lScissorTest.isDifferent(newState.lScissorTest))
			updateScissorTest(gl2,newState);
		
		if(this.lStencilTest.isDifferent(newState.lStencilTest))
			updateStencilTest(gl2,newState);
		
		if(this.lTexture.isDifferent(newState.lTexture))
			updateTexture(gl2,newState);
		
		if(this.lDepthMask != newState.lDepthMask)
			updateDepthMask(gl2,newState);
	}
	
	private void updateDepthMask(GL2 gl2, GvRenderState newState) {
		lDepthMask = newState.lDepthMask;
		gl2.glDepthMask(lDepthMask);
	}

	private void updateStencilTest(GL2 gl2, GvRenderState newState) {
		lStencilTest.set(newState.lStencilTest);
		if(lStencilTest.lEnabled)
			gl2.glEnable(GL2.GL_STENCIL_TEST);
		else
			gl2.glDisable(GL2.GL_STENCIL_TEST);
	}

	private void updateScissorTest(GL2 gl2, GvRenderState newState) {
		lScissorTest.set(newState.lScissorTest);
		if(lScissorTest.lEnabled)
			gl2.glEnable(GL2.GL_SCISSOR_TEST);
		else
			gl2.glDisable(GL2.GL_SCISSOR_TEST);
	}

	private void updateLighting(GL2 gl2, GvRenderState newState) {
		lLighting.set(newState.lLighting);
		if(lLighting.lEnabled)
			gl2.glEnable(GL2.GL_LIGHTING);
		else
			gl2.glDisable(GL2.GL_LIGHTING);
	}

	private void updateFaceCulling(GL2 gl2, GvRenderState newState)
	{
		lFaceCulling.set(newState.lFaceCulling);
		if(lFaceCulling.lEnabled)
			gl2.glEnable(GL2.GL_CULL_FACE);
		else
			gl2.glDisable(GL2.GL_CULL_FACE);
	}
	
	private void updateTexture(GL2 gl2, GvRenderState newState)
	{
		lTexture.set(newState.lTexture);
		if(lTexture.lEnabled)
			gl2.glEnable(GL2.GL_TEXTURE_2D);
		else
			gl2.glDisable(GL2.GL_TEXTURE_2D);
	}
	
	private void updateRasterizationMode(GL2 gl2, GvRenderState newState)
	{
		lRasterizationMode.set(newState.lRasterizationMode);
		
		int face, mode;
		
		if(lRasterizationMode.lFace == GvRasterizationMode.GvRasterFace.FRONT)
			face = GL2.GL_FRONT;
		else if(lRasterizationMode.lFace == GvRasterizationMode.GvRasterFace.BACK)
			face = GL2.GL_BACK;
		else
			face = GL2.GL_FRONT_AND_BACK;
		
		if(lRasterizationMode.lMode == GvRasterizationMode.GvRasterMode.FILL)
			mode = GL2.GL_FILL;
		else if(lRasterizationMode.lMode == GvRasterizationMode.GvRasterMode.LINE)
			mode = GL2.GL_LINE;
		else
			mode = GL2.GL_POINT;
		
		gl2.glPolygonMode(face,mode);
	}
	
	private void updateDepthTest(GL2 gl2, GvRenderState newState)
	{
		lDepthTest.set(newState.lDepthTest);
		
		if(lDepthTest.lEnabled)
			gl2.glEnable(GL2.GL_DEPTH_TEST);
		else
			gl2.glDisable(GL2.GL_DEPTH_TEST);
		
		int func;
		
		if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.LESS_THAN_OR_EQUAL)
			func = GL2.GL_LEQUAL;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.LESS)
			func = GL2.GL_LESS;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.ALWAYS)
			func = GL2.GL_ALWAYS;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.EQUAL)
			func = GL2.GL_EQUAL;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.GREATER)
			func = GL2.GL_GREATER;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.GREATER_THAN_OR_EQUAL)
			func = GL2.GL_GEQUAL;
		else if(lDepthTest.lFunction == GvDepthTest.GvDepthTestFunction.NEVER)
			func = GL2.GL_NEVER;
		else
			func = GL2.GL_NOTEQUAL;
		
		gl2.glDepthFunc(func);
	}
}

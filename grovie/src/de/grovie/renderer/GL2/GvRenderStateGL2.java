package de.grovie.renderer.GL2;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.grovie.renderer.renderstate.GvRasterizationMode;
import de.grovie.renderer.renderstate.GvRenderState;

public class GvRenderStateGL2 extends GvRenderState {
	
	static byte[] resultByte;
	static ByteBuffer resultByteBuffer;
	static int[] resultIntx2;
	static IntBuffer resultIntBufferx2;
	
	public GvRenderStateGL2()
	{
		super();
		
		resultByte = new byte[1];
		resultByteBuffer = ByteBuffer.wrap(resultByte);
		
		resultIntx2 = new int[2];
		resultIntBufferx2 = IntBuffer.wrap(resultIntx2);
		
	}
	
	private boolean isDiff(int glConstant, boolean newState, GL2 gl2)
	{
		if(gl2.glIsEnabled(glConstant) != newState)
			return true;
		
		return false;
	}
	
	private boolean isDiffRasterizationMode(GvRasterizationMode rasterMode, GL2 gl2)
	{
		gl2.glGetIntegerv(GL2.GL_POLYGON_MODE, resultIntBufferx2);
		if((resultIntx2[0]==GL2.GL_FRONT)&&(rasterMode.lFace!=GvRasterizationMode.GvRasterFace.FRONT))
			return true;
		if((resultIntx2[0]==GL2.GL_FRONT_AND_BACK)&&(rasterMode.lFace!=GvRasterizationMode.GvRasterFace.FRONT_AND_BACK))
			return true;
		if((resultIntx2[0]==GL2.GL_BACK)&&(rasterMode.lFace!=GvRasterizationMode.GvRasterFace.BACK))
			return true;
		
		if((resultIntx2[1]==GL2.GL_FILL)&&(rasterMode.lMode!=GvRasterizationMode.GvRasterMode.FILL))
			return true;
		if((resultIntx2[1]==GL2.GL_LINE)&&(rasterMode.lMode!=GvRasterizationMode.GvRasterMode.LINE))
			return true;
		if((resultIntx2[1]==GL2.GL_POINT)&&(rasterMode.lMode!=GvRasterizationMode.GvRasterMode.POINT))
			return true;
		
		return false;
	}
	
	/**
	 * Checks current OpenGL states against required state. Update if different.
	 */
	@Override
	public void update(GvRenderState newState, Object context) {
		GL2 gl2 = (GL2)context;
		
		if(isDiff(GL2.GL_CULL_FACE,newState.lFaceCulling.lEnabled,gl2))
			updateFaceCulling(gl2,newState);
		
		if(isDiffRasterizationMode(newState.lRasterizationMode, gl2))
			updateRasterizationMode(gl2,newState);
		
		if(isDiff(GL2.GL_DEPTH_TEST,newState.lDepthTest.lEnabled,gl2))
			updateDepthTest(gl2,newState);
		
		if(isDiff(GL2.GL_LIGHTING,newState.lLighting.lEnabled,gl2))
			updateLighting(gl2,newState);
		
		if(isDiff(GL2.GL_SCISSOR_TEST,newState.lScissorTest.lEnabled,gl2))
			updateScissorTest(gl2,newState);
		
		if(isDiff(GL2.GL_STENCIL_TEST,newState.lStencilTest.lEnabled,gl2))
			updateStencilTest(gl2,newState);
		
		//if(isDiff(GL2.GL_TEXTURE_2D,newState.lTexture.lEnabled,gl2))
			updateTexture(gl2,newState);
		
		gl2.glGetBooleanv(GL2.GL_DEPTH_WRITEMASK, resultByteBuffer);
		if((resultByte[0]==1)!=lDepthMask)
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
		{
			gl2.glEnable(GL2.GL_TEXTURE_2D);
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
			gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		}
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
	}
}

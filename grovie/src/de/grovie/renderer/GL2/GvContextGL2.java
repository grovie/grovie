package de.grovie.renderer.GL2;

import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvVertexArray;

public class GvContextGL2 extends GvContext {

	public GvContextGL2(GvRenderer renderer) {
		super(renderer);
	}

	@Override
	public GvVertexArray createVertexArray(GvVertexArray vao) throws GvExRendererVertexArray {
		try
		{
			//get reference to jogl gl2
			GL2 gl2 = ((GvIllustratorGL2)lRenderer.getIllustrator()).getGL2();
			
			//int buffer for storing id given by opengl
			int vaoId[] = new int[1];
			IntBuffer vaoIdBuffer = IntBuffer.wrap(vaoId);
			
			// Create 1 Vertex Array Object, id set into vaoIdBuffer
			gl2.glGenVertexArrays(1, vaoIdBuffer);
		
			//set created VAO id into specified wrapper object
			vao.setId(vaoId[0]);
			
			return vao;
		}
		catch(Exception e)
		{
			throw new GvExRendererVertexArray("Error generating VAO.");
		}
	}

	@Override
	public GvDrawGroup createDrawGroup() throws GvExRendererDrawGroup {
		return new GvDrawGroup();
	}

	@Override
	public GvBufferSet createBufferSet()
			throws GvExRendererBufferSet {
		return (GvBufferSet)new GvBufferSetGL2();
	}

}

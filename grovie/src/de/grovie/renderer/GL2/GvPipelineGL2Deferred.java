package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvPipeline;
import de.grovie.renderer.GvRenderer;

public class GvPipelineGL2Deferred extends GvPipeline{

	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	
	public GvPipelineGL2Deferred(GvRenderer renderer, GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu) throws GvExRendererPassShaderResource
	{
		super(renderer);
		this.lglAutoDrawable = glAutoDrawable;
		this.lgl2 = gl2;
		this.lglu = glu;
		init();
	}

	public void init() throws GvExRendererPassShaderResource {
		// TODO Add 3 passes for deferred shading pipeline
		
		for(int i=0; i<lPasses.size(); ++i)
		{
			lPasses.get(i).init();
		}
	}

	@Override
	public void reshape(int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		
	}
}

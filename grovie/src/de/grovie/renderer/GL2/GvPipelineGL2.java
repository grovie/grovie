package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvPipeline;
import de.grovie.renderer.GvRenderer;

public class GvPipelineGL2 extends GvPipeline{

	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	
	public GvPipelineGL2(GvRenderer renderer, GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu) throws GvExRendererPassShaderResource
	{
		super(renderer);
		this.lglAutoDrawable = glAutoDrawable;
		this.lgl2 = gl2;
		this.lglu = glu;
		init();
	}
	
	public void init() throws GvExRendererPassShaderResource {
		this.addPass(new GvPassGL2(lglAutoDrawable, lgl2, lglu, lRenderer));
		
		for(int i=0; i<lPasses.size(); ++i)
		{
			lPasses.get(i).init();
		}
	}

	@Override
	public void reshape(int x, int y, int width, int height) {
		for(int i=0; i<lPasses.size(); ++i)
		{
			lPasses.get(i).reshape(x,y,width,height);
		}
	}
}

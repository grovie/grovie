package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvRenderer;

public class GvIllustratorGL2  extends GvIllustrator implements GLEventListener{
	
	GLAutoDrawable lGLAutoDrawable;
	GL2 lGL2;
	
	public GvIllustratorGL2(GvRenderer renderer)
	{
		super(renderer);
		
		lGLAutoDrawable = null;
		lGL2 = null;
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		
		lGLAutoDrawable = glAutoDrawable;
		lGL2 = glAutoDrawable.getGL().getGL2();
		
		display();
		
//		OneTriangleAWT.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight() );
//		TestRenderer.render( glAutoDrawable, glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight(),
//				this.lRenderer);
//		TestRendererTex.render( glAutoDrawable, glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight(),
//				this.lRenderer);
		
//		TestRendererDeferred.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight(),
//				this.lRenderer);
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		
		lGLAutoDrawable = glAutoDrawable;
		lGL2 = glAutoDrawable.getGL().getGL2();
		
		init();
		
//		TestRenderer.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
//		TestRendererTex.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
//		TestRendererDeferred.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		
		lGLAutoDrawable = glAutoDrawable;
		lGL2 = glAutoDrawable.getGL().getGL2();
		
		reshape(x,y,width,height);
		
//		OneTriangleAWT.setup( glAutoDrawable.getGL().getGL2(),
//				width,
//				height );
//		TestRenderer.reshape( glAutoDrawable.getGL().getGL2(),
//				width,
//				height,
//				this.lRenderer);
//		TestRendererTex.reshape( glAutoDrawable.getGL().getGL2(),
//				width,
//				height,
//				this.lRenderer);
//		TestRendererDeferred.reshape( glAutoDrawable.getGL().getGL2(),
//				width,
//				height,
//				this.lRenderer);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		lGLAutoDrawable = glAutoDrawable;
		lGL2 = glAutoDrawable.getGL().getGL2();
		
		dispose();
	}

	@Override
	public void reshape(int x, int y, int width, int height) {

	}

	@Override
	public void init() {
		//disable JOGL auto buffer swap to allow timing frame draw
		lGLAutoDrawable.setAutoSwapBufferMode(false);
		
		//check if multi-color attachment FBOs are supported. assign pipeline
		if(lGLAutoDrawable.getContext().hasFullFBOSupport())
			lPipeline = new GvPipelineGL2Deferred();
		else
			lPipeline = new GvPipelineGL2();
	}
	
	@Override
	public void display2DOverlay() {
		
	}
	
	@Override
	public void displayEnd() {
		//swap draw buffers
		lGLAutoDrawable.swapBuffers();		
	}

	@Override
	public void dispose() {
		// TODO: Call upon engine shutdown. delete allocated memory where possible.
	}

	public GLAutoDrawable getGLAutoDrawable() {
		return lGLAutoDrawable;
	}

	public void setGLAutoDrawable(GLAutoDrawable glAutoDrawable) {
		this.lGLAutoDrawable = glAutoDrawable;
	}

	public GL2 getGL2() {
		return lGL2;
	}

	public void setGL2(GL2 gl2) {
		this.lGL2 = gl2;
	}
}

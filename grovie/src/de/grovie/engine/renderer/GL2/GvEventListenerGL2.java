package de.grovie.engine.renderer.GL2;

import javax.media.opengl.GLAutoDrawable;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.test.engine.renderer.TestRenderer;
import de.grovie.test.engine.renderer.TestRendererDeferred;
import de.grovie.test.engine.renderer.TestRendererTex;

public class GvEventListenerGL2  extends GvEventListener {

	public GvEventListenerGL2(GvRenderer renderer)
	{
		super(renderer);
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
//		OneTriangleAWT.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight() );
//		TestRenderer.render( glAutoDrawable, glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight(),
//				this.lRenderer);
		TestRendererTex.render( glAutoDrawable, glAutoDrawable.getGL().getGL2(), 
				glAutoDrawable.getWidth(), 
				glAutoDrawable.getHeight(),
				this.lRenderer);
		
//		TestRendererDeferred.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight(),
//				this.lRenderer);
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		glAutoDrawable.setAutoSwapBufferMode(false);
//		TestRenderer.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
		TestRendererTex.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
//		TestRendererDeferred.init(glAutoDrawable.getGL().getGL2(),this.lRenderer);
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
//		OneTriangleAWT.setup( glAutoDrawable.getGL().getGL2(),
//				width,
//				height );
//		TestRenderer.reshape( glAutoDrawable.getGL().getGL2(),
//				width,
//				height,
//				this.lRenderer);
		TestRendererTex.reshape( glAutoDrawable.getGL().getGL2(),
				width,
				height,
				this.lRenderer);
//		TestRendererDeferred.reshape( glAutoDrawable.getGL().getGL2(),
//				width,
//				height,
//				this.lRenderer);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		// TODO Auto-generated method stub
		
	}
}

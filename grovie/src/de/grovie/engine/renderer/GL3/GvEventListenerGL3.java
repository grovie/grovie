package de.grovie.engine.renderer.GL3;

import javax.media.opengl.GLAutoDrawable;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.test.engine.renderer.TestRenderer;

public class GvEventListenerGL3  extends GvEventListener {

	public GvEventListenerGL3(GvRenderer renderer)
	{
		super(renderer);
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
//		OneTriangleAWT.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight() );
		TestRenderer.render( glAutoDrawable.getGL().getGL2(), 
				glAutoDrawable.getWidth(), 
				glAutoDrawable.getHeight(),
				this.lRenderer);
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
//		OneTriangleAWT.setup( glAutoDrawable.getGL().getGL2(),
//				width,
//				height );
		TestRenderer.setup( glAutoDrawable.getGL().getGL2(),
				width,
				height );
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		// TODO Auto-generated method stub
		
	}
}

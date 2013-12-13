package de.grovie.engine.renderer.GL3;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.test.OneTriangleAWT;
import de.grovie.test.engine.renderer.TestRenderer;

public class GvEventListenerGL3  extends GvEventListener {

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
//		OneTriangleAWT.render( glAutoDrawable.getGL().getGL2(), 
//				glAutoDrawable.getWidth(), 
//				glAutoDrawable.getHeight() );
		TestRenderer.render( glAutoDrawable.getGL().getGL2(), 
				glAutoDrawable.getWidth(), 
				glAutoDrawable.getHeight() );
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

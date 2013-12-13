package de.grovie.engine.renderer.GL3;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.test.OneTriangleAWT;

public class GvEventListenerGL3  extends GvEventListener {

	@Override
	public void display(GLAutoDrawable arg0) {
		OneTriangleAWT.render( arg0.getGL().getGL2(), arg0.getWidth(), arg0.getHeight() );
	}
	
	@Override
	public void init(GLAutoDrawable arg0) {
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
		OneTriangleAWT.setup( arg0.getGL().getGL2(), width, height );
	}

	@Override
	public void dispose(GLAutoDrawable gl) {
		// TODO Auto-generated method stub
		
	}
}

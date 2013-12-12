package de.grovie.sandbox;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import de.grogra.grovie.test.OneTriangleAWT;
import de.grovie.engine.GvEventListener;

public class GvGLEventListener extends GvEventListener implements GLEventListener {

	@Override
	public void display(GLAutoDrawable arg0) {
		super.display((Object)arg0);
		OneTriangleAWT.render( arg0.getGL().getGL2(), arg0.getWidth(), arg0.getHeight() );
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		super.dispose((Object)arg0);
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		super.init((Object)arg0);
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
		super.reshape((Object)arg0, x, y, width, height);
		OneTriangleAWT.setup( arg0.getGL().getGL2(), width, height );
	}

}

package de.grovie.engine.renderer;

import javax.media.opengl.GLAutoDrawable;

public abstract class GvEventListener {

	public abstract void reshape(GLAutoDrawable gl, int x, int y, int width, int height);
	public abstract void init(GLAutoDrawable gl);
	public abstract void display(GLAutoDrawable gl);
	public abstract void dispose(GLAutoDrawable gl);
}

package de.grovie.engine.renderer;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public abstract class GvEventListener implements GLEventListener{

	protected GvRenderer lRenderer;
	
	public GvEventListener(GvRenderer renderer)
	{
		lRenderer = renderer;
	}
	
	public abstract void reshape(GLAutoDrawable gl, int x, int y, int width, int height);
	public abstract void init(GLAutoDrawable gl);
	public abstract void display(GLAutoDrawable gl);
	public abstract void dispose(GLAutoDrawable gl);
}

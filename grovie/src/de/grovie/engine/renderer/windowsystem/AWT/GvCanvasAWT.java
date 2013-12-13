package de.grovie.engine.renderer.windowsystem.AWT;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.windowsystem.GvCanvas;

public class GvCanvasAWT extends GLCanvas implements GvCanvas{

	public GvCanvasAWT(GLCapabilities glCapabilities) {
		super(glCapabilities);
	}

	@Override
	public void setEventListener(GLEventListener listener) {
		addGLEventListener(listener);
	}
	
}

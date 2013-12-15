package de.grovie.engine.renderer.windowsystem.AWT;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.windowsystem.GvCanvas;
import de.grovie.engine.renderer.windowsystem.GvIOListener;

public class GvCanvasAWT extends GLCanvas implements GvCanvas{

	public GvCanvasAWT(GLCapabilities glCapabilities) {
		super(glCapabilities);
	}

	@Override
	public void setEventListener(GLEventListener listener) {
		addGLEventListener(listener);
	}

	@Override
	public void setIOListener(GvIOListener IOListener) {
		this.addMouseListener((MouseListener)IOListener);
		this.addMouseMotionListener((MouseMotionListener)IOListener);
		this.addMouseWheelListener((MouseWheelListener)IOListener);
		this.addKeyListener((KeyListener)IOListener);
	}

	@Override
	public void redraw() {
		this.display();
	}
	
}

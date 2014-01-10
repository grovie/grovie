package de.grovie.renderer.windowsystem.swing;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLJPanel;

import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.windowsystem.GvCanvas;
import de.grovie.renderer.windowsystem.GvIOListener;

@SuppressWarnings("serial")
public class GvCanvasSwingGL extends GLJPanel implements GvCanvas{
	
	public GvCanvasSwingGL(GLCapabilities glCapabilities) {
		super(glCapabilities);
	}

	@Override
	public void setEventListener(GvIllustrator listener) {
		if(listener instanceof GLEventListener)
		{
			this.addGLEventListener((GLEventListener)listener);
		}
	}

	@Override
	public void setIOListener(GvIOListener IOListener) {
		this.addMouseListener((MouseListener)IOListener);
		this.addMouseMotionListener((MouseMotionListener)IOListener);
		this.addMouseWheelListener((MouseWheelListener)IOListener);
		this.addKeyListener((KeyListener)IOListener);
	}

	@Override
	public void refresh() {
		this.display();
	}
}
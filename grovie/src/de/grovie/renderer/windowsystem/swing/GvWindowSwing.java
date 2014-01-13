package de.grovie.renderer.windowsystem.swing;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import de.grovie.renderer.windowsystem.GvCanvas;
import de.grovie.renderer.windowsystem.GvWindow;

@SuppressWarnings("serial")
public class GvWindowSwing extends JFrame implements GvWindow {

	public GvWindowSwing(String windowTitle) {
		super(windowTitle);
	}

	@Override
	public void setCanvas(GvCanvas canvas) {
		getContentPane().add( (GvCanvasSwingGL)canvas, BorderLayout.CENTER );
	}

	@Override
	public void close() {
		this.setVisible(false);
		this.dispose();
	}
}

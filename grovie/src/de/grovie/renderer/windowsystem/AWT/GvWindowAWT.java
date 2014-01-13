package de.grovie.renderer.windowsystem.AWT;

import java.awt.Component;
import java.awt.Frame;

import de.grovie.renderer.windowsystem.GvCanvas;
import de.grovie.renderer.windowsystem.GvWindow;

@SuppressWarnings("serial")
public class GvWindowAWT extends Frame implements GvWindow {

	public GvWindowAWT(String windowTitle) {
		super(windowTitle);
	}

	@Override
	public void setCanvas(GvCanvas canvas) {
		this.add((Component) canvas);
	}

	@Override
	public void close() {
		this.setVisible(false);
		this.dispose();
	}
}

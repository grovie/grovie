package de.grovie.engine.renderer.windowsystem.AWT;

import java.awt.Component;
import java.awt.Frame;

import de.grovie.engine.renderer.windowsystem.GvCanvas;
import de.grovie.engine.renderer.windowsystem.GvWindow;

public class GvWindowAWT extends Frame implements GvWindow {

	public GvWindowAWT(String windowTitle) {
		super(windowTitle);
	}

	@Override
	public void setCanvas(GvCanvas canvas) {
		this.add((Component) canvas);
	}
}

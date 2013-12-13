package de.grovie.engine.renderer.windowsystem;

import de.grovie.engine.renderer.GvEventListener;

public abstract class GvWindowSystem {
	
	protected GvCanvas lCanvas;
	protected GvWindow lWindow;
	
	public abstract GvWindowSystem getInstance(int width, 
			int height, 
			String windowTitle,
			GvEventListener eventListener);
	
	public GvCanvas getCanvas() {
		return lCanvas;
	}

	public void setCanvas(GvCanvas canvas) {
		this.lCanvas = canvas;
	}

	public GvWindow getWindow() {
		return lWindow;
	}

	public void setWindow(GvWindow window) {
		this.lWindow = window;
	}
}

package de.grovie.engine.renderer.windowsystem;

import de.grovie.engine.renderer.GvRenderer;

public abstract class GvWindowSystem {
	
	protected GvCanvas lCanvas;
	protected GvWindow lWindow;
	
	protected GvIOListener lIOListener;
	
	public abstract GvWindowSystem getInstance(GvRenderer renderer);
	
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

	public GvCanvas getlCanvas() {
		return lCanvas;
	}

	public void setlCanvas(GvCanvas lCanvas) {
		this.lCanvas = lCanvas;
	}

	public GvWindow getlWindow() {
		return lWindow;
	}

	public void setlWindow(GvWindow lWindow) {
		this.lWindow = lWindow;
	}

	public GvIOListener getIOListener() {
		return lIOListener;
	}

	public void setIOListener(GvIOListener IOListener) {
		this.lIOListener = IOListener;
	}

	
}

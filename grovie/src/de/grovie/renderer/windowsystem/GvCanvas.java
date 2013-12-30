package de.grovie.renderer.windowsystem;

import de.grovie.renderer.GvIllustrator;

public interface GvCanvas {

	public void setEventListener(GvIllustrator listener);
	public void setIOListener(GvIOListener IOListener);
	public void refresh();
}

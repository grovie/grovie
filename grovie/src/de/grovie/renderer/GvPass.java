package de.grovie.renderer;

/**
 * An abstract representation of a rendering pass.
 * 
 * @author yong
 *
 */
public abstract class GvPass {
	public abstract void start();
	public abstract void execute();
	public abstract void stop();
}

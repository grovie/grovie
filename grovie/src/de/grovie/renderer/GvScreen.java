package de.grovie.renderer;

/**
 * This class represents a screen, its dimensions and essential computations
 * 
 * @author yong
 *
 */
public class GvScreen {

	private int lScreenWidth;
	private int lScreenHeight;
	
	public GvScreen(int screenWidth, int screenHeight)
	{
		lScreenWidth = screenWidth;
		lScreenHeight = screenHeight;
	}
	
	public int getScreenWidth() {
		return lScreenWidth;
	}
	public void setScreenWidth(int screenWidth) {
		this.lScreenWidth = screenWidth;
	}
	public int getScreenHeight() {
		return lScreenHeight;
	}
	public void setScreenHeight(int screenHeight) {
		this.lScreenHeight = screenHeight;
	}
}

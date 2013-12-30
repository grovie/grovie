package de.grovie.renderer;

/**
 * This class represents a 2-dimensional texture.
 * 
 * @author yong
 *
 */
public class GvTexture2D {

	private int lId;		//id of texture

	public GvTexture2D(int id)
	{
		this.lId = id;
	}

	public int getId() {
		return lId;
	}
	public void setId(int id) {
		this.lId = id;
	}
}

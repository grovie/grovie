package de.grovie.renderer.GL2;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;

import de.grovie.renderer.GvTexture2D;

/**
 * This class is an OpenGL implementation of GvTexture2D.
 * The JOGL utility classes for textures are utilized.
 * 
 * @author yong
 *
 */
public class GvTexture2DGL2 extends GvTexture2D {

	private Texture lTexture;
	private TextureData lTextureData;
	
	public GvTexture2DGL2(int id) {
		super(id);
	}

	public GvTexture2DGL2(int id, Texture texture, TextureData textureData)
	{
		super(id);
		lTexture = texture;
		lTextureData = textureData;
	}

	public Texture getTexture() {
		return lTexture;
	}

	public void setTexture(Texture texture) {
		this.lTexture = texture;
	}

	public TextureData getTextureData() {
		return lTextureData;
	}

	public void setTextureData(TextureData textureData) {
		this.lTextureData = textureData;
	}
}

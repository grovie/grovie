package de.grovie.renderer.GL2;

import java.util.ArrayList;

import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroupMaterial;
import de.grovie.renderer.GvDrawGroupTexture;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

/**
 * This class is an OpenGL implementation of the GvRenderer class.
 * 
 * @author yong
 *
 */
public class GvRendererGL2 extends GvRenderer{

	private ArrayList<GvTexture2DGL2> lTextures;
	private ArrayList<GvMaterial> lMaterials;
	
	private GvDrawGroupMaterial lDrawGroupNonTex;
	private GvDrawGroupTexture lDrawGroupTex;
	
	public GvRendererGL2(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight) {
		super(windowSystem,windowTitle,windowWidth,windowHeight);
	}

	@Override
	public GvDevice createDevice() {
		return new GvDeviceGL2(this);
	}

	@Override
	public GvContext createContext() {
		return new GvContextGL2(this);
	}
	
	@Override
	public GvIllustrator createIllustrator() {
		return new GvIllustratorGL2(this);
	}

	
}

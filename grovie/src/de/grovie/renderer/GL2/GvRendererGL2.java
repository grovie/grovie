package de.grovie.renderer.GL2;

import java.util.ArrayList;

import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroup;
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
	
	//2 sets of VBO for swapping. 1 for rendering, 1 for real-time updating.
	private GvDrawGroup lDrawGroupRender;	//VBOs set for rendering
	private GvDrawGroup lDrawGroupUpdate;	//VBOs set for updating
	
	public GvRendererGL2(
			GvWindowSystem windowSystem, 
			String windowTitle, 
			int windowWidth, 
			int windowHeight) {
		
		super(windowSystem,windowTitle,windowWidth,windowHeight);
		
		lTextures = new ArrayList<GvTexture2DGL2>();
		lMaterials = new ArrayList<GvMaterial>();
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

	public void addTexture2D(GvTexture2DGL2 tex)
	{
		lTextures.add(tex);
	}
	
	public void addMaterial(GvMaterial mat)
	{
		lMaterials.add(mat);
	}
	
	/**
	 * Initiate draw groups. 2 sets of VBOs.
	 * 1 for rendering, 1 for real-time updating.
	 * This method should be invoked by the illustrator/pipeline
	 * after device creates all textures and all material types loaded.
	 */
	protected void initDrawGroups()
	{
		lDrawGroupRender = new GvDrawGroup();
		lDrawGroupUpdate = new GvDrawGroup();
		
		lDrawGroupRender.initGroups(lTextures.size(), lMaterials.size());
		lDrawGroupUpdate.initGroups(lTextures.size(), lMaterials.size());
	}
}

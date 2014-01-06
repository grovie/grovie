package de.grovie.engine.concurrent;

import java.io.InputStream;
import java.util.ArrayList;

import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvRenderer;

public class GvMsgRenderSceneStaticData implements GvMsgRender {

	private ArrayList<GvMaterial> lMaterials;
	private ArrayList<InputStream> lTextures;
	private ArrayList<String> lTextureFileExts;
	
	public GvMsgRenderSceneStaticData (ArrayList<GvMaterial> materials, 
			ArrayList<InputStream> textures,
			ArrayList<String> textureFileExts)
	{
		lMaterials = materials;
		lTextures = textures;
		lTextureFileExts = textureFileExts;
	}
	
	@Override
	public void process(GvRenderer target) {
		target.initSceneStaticData(lMaterials, lTextures,lTextureFileExts);
		target.sendCamera();
		target.sendUpdateBuffer();
	}

}

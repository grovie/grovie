package de.grovie.engine.concurrent;

import java.io.InputStream;
import java.util.ArrayList;

import de.grovie.data.GvData;
import de.grovie.renderer.GvMaterial;

public class GvMsgDataSceneStaticData implements GvMsgData {

	private ArrayList<GvMaterial> lMaterials;
	private ArrayList<InputStream> lTextures;
	private ArrayList<String> lTextureFileExts;
	
	public GvMsgDataSceneStaticData (ArrayList<GvMaterial> materials, 
			ArrayList<InputStream> textures,
			ArrayList<String> textureFileExts)
	{
		lMaterials = materials;
		lTextures = textures;
		lTextureFileExts = textureFileExts;
	}
	
	@Override
	public void process(GvData target) {
		target.sendSceneStaticData(lMaterials, lTextures,lTextureFileExts);
	}
}

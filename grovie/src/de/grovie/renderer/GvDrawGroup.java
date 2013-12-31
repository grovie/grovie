package de.grovie.renderer;

import java.util.ArrayList;

/**
 * This class contains a group of geometry that share a common 
 * texture, material or primitive type.
 * Geometry to be drawn are grouped by instances of this class to reduce
 * overhead of calls to the OpenGL server.
 * 
 * @author yong
 *
 */
public class GvDrawGroup {

	public static final int GROUP_NON_TEXTURED		= 0;
	public static final int GROUP_TEXTURED 			= 1;
	
	private ArrayList<GvDrawGroup> lGroups;
	
	public GvDrawGroup()
	{
		lGroups = new ArrayList<GvDrawGroup>();
	}
	
	public GvDrawGroup addDrawGroup(GvDrawGroup drawGroup)
	{
		lGroups.add(drawGroup);
		return drawGroup;
	}
	
	/**
	 * Initialize this draw group as either a rendering or an updating group.
	 * This group then contains a group of textured vertices and non-textured
	 * vertices.
	 * 
	 * @param textureCount
	 * @param materialCount
	 */
	public void initGroups(int textureCount, int materialCount)
	{
		//non-textured group
		this.addDrawGroup(new GvDrawGroup()).initGroupsMaterials(materialCount);
		
		//textured group
		this.addDrawGroup(new GvDrawGroup()).initGroupsTextures(textureCount, materialCount);
		
		//dual-textured group
		//future extension
	}
	
	/**
	 * Initialize this draw group as a textured group.
	 * This group then contains groups of vertices, each with a different texture.
	 * 
	 * @param textureCount
	 * @param materialCount
	 */
	public void initGroupsTextures(int textureCount, int materialCount)
	{
		for(int i=0; i<textureCount; ++i)
		{
			this.addDrawGroup(new GvDrawGroup()).initGroupsMaterials(materialCount);
		}
	}
	
	/**
	 * Initialize this draw group as groups of vertices, each with a different material.
	 * 
	 * @param materialCount
	 */
	public void initGroupsMaterials(int materialCount)
	{
		for(int i=0; i<materialCount; ++i)
		{
			this.addDrawGroup(new GvDrawGroup()).initGroupsPrimitives();
		}
	}
	
	/**
	 * Initialize this draw group as groups of vertices, each with a different 
	 * primitive type.
	 */
	public void initGroupsPrimitives()
	{
		for(int i=0; i<GvPrimitive.PRIMITIVE_COUNT; ++i)
		{
			this.addDrawGroup(new GvDrawGroupBufferSet());
		}
	}
}

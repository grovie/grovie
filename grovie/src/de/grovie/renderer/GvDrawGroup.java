package de.grovie.renderer;

import java.util.ArrayList;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsgDataNewBufferSet;
import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;

/**
 * This class contains a group of geometry that share a common 
 * texture, material or primitive type.
 * Geometry to be drawn are grouped by instances of this class to reduce
 * overhead of calls to the OpenGL server.
 * 
 * @author yong
 *
 */
public class GvDrawGroup implements GvMsgDataNewBufferSet {

	public static final int GROUP_NON_TEXTURED			= 0;
	public static final int GROUP_TEXTURED 				= 1;
	
	public static final int GROUP_NON_BACKFACE_CULLED 	= 0;
	public static final int GROUP_BACKFACE_CULLED		= 1;
	
	
	protected ArrayList<GvDrawGroup > lGroups;
	
	public GvDrawGroup()
	{
		lGroups = new ArrayList<GvDrawGroup >();
	}
	
	public GvDrawGroup addGroup(GvDrawGroup drawGroup)
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
	 * @param device
	 * @throws GvExRendererDrawGroup 
	 * @throws GvExRendererBufferSet 
	 */
	public void initGroups(int textureCount, int materialCount, GvContext context) 
			throws GvExRendererDrawGroup, GvExRendererBufferSet
	{
		//non-textured group
		this.addGroup(context.createDrawGroup()).initGroupsMaterials(materialCount, context);
		
		//textured group
		this.addGroup(context.createDrawGroup()).initGroupsTextures(textureCount, materialCount, context);
		
		//dual-textured group
		//future extension
	}
	
	/**
	 * Initialize this draw group as a textured group.
	 * This group then contains groups of vertices, each with a different texture.
	 * 
	 * @param textureCount
	 * @param materialCount
	 * @param device
	 * @throws GvExRendererBufferSet 
	 */
	private void initGroupsTextures(int textureCount, int materialCount, GvContext context)
			throws GvExRendererDrawGroup, GvExRendererBufferSet
	{
		for(int i=0; i<textureCount; ++i)
		{
			this.addGroup(context.createDrawGroup()).initGroupsMaterials(materialCount, context);
		}
	}
	
	/**
	 * Initialize this draw group as groups of vertices, each with a different material.
	 * 
	 * @param materialCount
	 * @param device
	 * @throws GvExRendererBufferSet 
	 */
	private void initGroupsMaterials(int materialCount, GvContext context)
			throws GvExRendererDrawGroup, GvExRendererBufferSet
	{
		for(int i=0; i<materialCount; ++i)
		{
			this.addGroup(context.createDrawGroup()).initGroupsPrimitives(context);
		}
	}
	
	/**
	 * Initialize this draw group as groups of vertices, each with a different 
	 * primitive type.
	 * 
	 * @param device
	 * @throws GvExRendererBufferSet 
	 */
	private void initGroupsPrimitives(GvContext context)
			throws GvExRendererDrawGroup, GvExRendererBufferSet
	{
		for(int i=0; i<GvPrimitive.PRIMITIVE_COUNT; ++i)
		{
			this.addGroup(context.createDrawGroup()).initGroupsBackFaceCull(context);
		}
	}
	
	/**
	 * Initialize this draw group as 2 groups of vertices, one with
	 * back-face culling enabled, the other with back-face culling disabled.
	 * 
	 * @param device
	 * @throws GvExRendererBufferSet 
	 */
	private void initGroupsBackFaceCull(GvContext context) 
			throws GvExRendererBufferSet
	{
		this.addGroup(context.createBufferSet()); //single-sided primitives, culling enabled
		this.addGroup(context.createBufferSet()); //dual-sided primitives, culling disabled
	}
	
	/**
	 * This method searches for the categorized set of VBO/VAO for inserting geometry.
	 * @param isTextured
	 * @param textureIndex
	 * @param materialIndex
	 * @param primitiveIndex
	 * @param isCulled
	 * @return a set of VBO/VAO to add geometry to
	 * @throws GvExRendererDrawGroupRetrieval 
	 */
	public GvBufferSet getBufferSet(
			boolean isTextured,
			int textureIndex,
			int materialIndex,
			int primitiveIndex,
			boolean isCulled) throws GvExRendererDrawGroupRetrieval
	{
		try{
			GvDrawGroup group = this.getGroup(isTextured);
			
			if(isTextured)
				group = group.getGroupTexture(textureIndex);
			
			return (GvBufferSet)group.
					getGroupMaterial(materialIndex
							).getGroupPrimitive(primitiveIndex
									).getGroupBackFaceCull(isCulled)
									;
		}
		catch(Exception e)
		{
			throw new GvExRendererDrawGroupRetrieval(
					"Error retrieving DrawGroup buffer set.");
		}
	}
	
	private GvDrawGroup getGroup(boolean isTextured)
	{
		if(isTextured)
			return lGroups.get(GROUP_TEXTURED);
		else
			return lGroups.get(GROUP_NON_TEXTURED);
	}
	
	private GvDrawGroup getGroupTexture(int textureIndex)
	{
		return lGroups.get(textureIndex);
	}
	
	private GvDrawGroup getGroupMaterial(int materialIndex)
	{
		return lGroups.get(materialIndex);
	}
	
	private GvDrawGroup getGroupPrimitive(int primitiveIndex)
	{
		return lGroups.get(primitiveIndex);
	}
	
	private GvDrawGroup getGroupBackFaceCull(boolean isCulled)
	{
		if(isCulled)
			return lGroups.get(GROUP_BACKFACE_CULLED);
		else
			return lGroups.get(GROUP_NON_BACKFACE_CULLED);
	}
	
	/**
	 * Recursive call to clear buffers
	 */
	public void clear(Object libraryAPI)
	{
		for(int i=0; i<lGroups.size(); ++i)
		{
			lGroups.get(i).clear(libraryAPI);
		}
	}
	
	/**
	 * Recursive to update hardware buffers
	 * @throws GvExRendererVertexArray 
	 * @throws GvExRendererVertexBuffer 
	 * @throws GvExRendererIndexBuffer 
	 */
	public void update(Object libraryAPI, GvDevice device) throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer
	{
		for(int i=0; i<lGroups.size(); ++i)
		{
			lGroups.get(i).update(libraryAPI, device);
		}
	}
	
	/**
	 * Recursive to update VAOs
	 * @param renderer
	 * @throws GvExRendererVertexArray 
	 */
	public void updateVAO(GvRenderer renderer) throws GvExRendererVertexArray
	{
		for(int i=0; i<lGroups.size(); ++i)
		{
			lGroups.get(i).updateVAO(renderer);
		}
	}

	@Override
	public void process(GvData target) {
		target.receiveBufferSet(this);
	}
	
	public boolean isEmpty()
	{
		if(lGroups.size()==0)
			return true;
		
		return false;
	}
}

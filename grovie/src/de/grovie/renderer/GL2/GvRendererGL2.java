package de.grovie.renderer.GL2;

import java.util.ArrayList;

import javax.media.opengl.GL2;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.renderer.GvAnimator;
import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.renderstate.GvRenderState;
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
			int windowHeight,
			GvMsgQueue<GvRenderer> queueIn,
			GvMsgQueue<GvData> queueOutData) {
		
		super(windowSystem,windowTitle,windowWidth,windowHeight,queueIn,queueOutData);
		
		lTextures = new ArrayList<GvTexture2DGL2>();
		lMaterials = new ArrayList<GvMaterial>();
		
		lDrawGroupRender = new GvDrawGroup();
		lDrawGroupUpdate = new GvDrawGroup();
		
		lRenderState = new GvRenderStateGL2();
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
	
	@Override
	public GvAnimator createAnimator() {
		return new GvAnimatorGL2();
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
	 * @throws GvExRendererDrawGroup 
	 * @throws GvExRendererBufferSet 
	 */
	protected void initDrawGroups() throws GvExRendererDrawGroup, GvExRendererBufferSet
	{
		lDrawGroupRender.initGroups(lTextures.size(), lMaterials.size(), lContext);
		lDrawGroupUpdate.initGroups(lTextures.size(), lMaterials.size(), lContext);
	}
	
	public GvDrawGroup getDrawGroupRender()
	{
		return lDrawGroupRender;
	}
	
	public GvDrawGroup getDrawGroupUpdate()
	{
		return lDrawGroupUpdate;
	}
	
	public void swapBuffers()
	{
		GvDrawGroup tempDrawGrp = lDrawGroupRender;
		lDrawGroupRender = lDrawGroupUpdate;
		lDrawGroupUpdate = tempDrawGrp;
	}

	public ArrayList<GvTexture2DGL2> getTextures() {
		return lTextures;
	}

	public void setTextures(ArrayList<GvTexture2DGL2> textures) {
		this.lTextures = textures;
	}

	public ArrayList<GvMaterial> getMaterials() {
		return lMaterials;
	}

	public void setMaterials(ArrayList<GvMaterial> materials) {
		this.lMaterials = materials;
	}

	@Override
	public void updateRenderState(GvRenderState newState, Object context) {
		lRenderState.update(newState, (GL2)context);
	}
}

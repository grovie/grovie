package de.grovie.renderer.GL2;

import java.io.InputStream;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsgQueue;
import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererTexture2D;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
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
		return new GvDeviceGL2();
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

	@Override
	public void swapBuffers()
	{
		try {

			//update VBOs and IBOs
			lDrawGroupUpdate.update(((GvIllustratorGL2)getIllustrator()).getGL2(), lDevice);

			//update VAOs
			lDrawGroupUpdate.updateVAO(this);

			//clean current rendering draw groups
			lDrawGroupRender.clear(((GvIllustratorGL2)getIllustrator()).getGL2());

		} catch (GvExRendererVertexArray e) {
			System.out.println("Error updating buffers before draw group swap.");
			return;
		} catch (GvExRendererVertexBuffer e) {
			System.out.println("Error updating buffers before draw group swap.");
			return;
		} catch (GvExRendererIndexBuffer e) {
			System.out.println("Error updating buffers before draw group swap.");
			return;
		}

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

	@Override
	public void sendUpdateBuffer() {
		lQueueOutData.offer(lDrawGroupUpdate);
	}

	@Override
	public void initSceneStaticData(ArrayList<GvMaterial> materials,
			ArrayList<InputStream> textures, ArrayList<String> textureFileExts) {

		for(int i=0; i<materials.size(); ++i)
		{
			addMaterial(materials.get(i));
		}

		try
		{
			GL2 gl2 = ((GvIllustratorGL2)this.getIllustrator()).getGL2();

			if(textureFileExts.size()!=textures.size())
				throw new GvExRendererTexture2D("Unmatching number of textures and file extensions.");

			for(int j=0; j<textures.size(); ++j)
			{
				InputStream stream = textures.get(j);
				String fileExt = textureFileExts.get(j);
				addTexture2D((GvTexture2DGL2)getDevice().createTexture2D(stream, fileExt,gl2));
			}

			//create drawing groups/categories based on static scene information, 
			//.e.g number of textures, materials, etc.
			initDrawGroups();
		}
		catch(GvExRendererTexture2D e)
		{
			System.out.println(e.getMessage());
		}
		catch (GvExRendererDrawGroup e) {
			e.printStackTrace();
		}
		catch(GvExRendererBufferSet e) {
			e.printStackTrace();
		}

		//	private void receiveMaterialsTextures(ArrayList<GvMaterial> materials, ArrayList<InputStream> textures) throws GvExRendererVertexBuffer, GvExRendererIndexBuffer, GvExRendererVertexArray, FileNotFoundException, GvExRendererTexture2D, GvExRendererDrawGroup, GvExRendererBufferSet, GvExRendererDrawGroupRetrieval
		//	{
		//		



		//		//FOR DEBUG
		//		//Test update scenario
		//		GvDrawGroup drawGrpUpdate = rendererGL2.getDrawGroupUpdate();
		//
		//		//Test geometry
		//		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
		//		GvGeometry geom = new GvGeometry();
		//		GvImporterObj.load(path, geom);
		//		int indices[] = geom.getIndices();
		//		float vertices[] = geom.getVertices();
		//		float normals[] = geom.getNormals();
		//
		//		GvGeometryTex geomBoxTex = TestRendererTex.getTexturedBox();
		//		GvGeometryTex geomTube = TestRendererTex.getTube(1, 20, 10, 1);
		//		GvGeometryTex geomPoints = TestRendererTex.getPoints(1000);
		//		
		//		//send geom CPU buffers - simulate action 2 by foreign thread after receiving
		//		//msg to update buufers
		//		GvBufferSet bufferSet;
		//		//send geometry to categorized draw groups //TODO: discard unnecessary listing of geometry in buffer sets
		//		bufferSet = drawGrpUpdate.getBufferSet(false, -1, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
		//		bufferSet.insertGeometry(vertices, normals, indices);
		//
		//		bufferSet = drawGrpUpdate.getBufferSet(true, 0, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
		//		bufferSet.insertGeometry(geomBoxTex.getVertices(), geomBoxTex.getNormals(), geomBoxTex.getIndices(), geomBoxTex.getUv());
		//
		//		bufferSet = drawGrpUpdate.getBufferSet(true, 1, 0, GvPrimitive.PRIMITIVE_TRIANGLE_STRIP, true);
		//		bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices(), geomTube.getUv());
		//		
		//		bufferSet = drawGrpUpdate.getBufferSet(false, -1, 1, GvPrimitive.PRIMITIVE_POINT, true);
		//		bufferSet.insertGeometry(geomPoints.getVertices(), geomPoints.getNormals(), geomPoints.getIndices());
		//		
		//		lgl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		//		lgl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		//		
		//		//VBO/IBO - OpenGL server-side
		//		//send geom to hardware buffers - simulate action 3 by foreign thread
		//		//TODO: integrate this with previous geometry insertion step
		//		drawGrpUpdate.update(lgl2, rendererGL2.getDevice());
		//
		//		//MESSAGE sent to rendering thread to swap buffers
		//
		//		//VAOs - OpenGL client-side - 
		//		//rendering thread action, action 1 after receiving msg to swap VBOs
		//		drawGrpUpdate.updateVAO(rendererGL2);
		//
		//		//VBO swap buffers - 
		//		//rendering thread action, action 2 after receiving msg to swap VBOs
		//		rendererGL2.swapBuffers();
		//
		//		drawGrpUpdate = rendererGL2.getDrawGroupUpdate();
		//		//MESSAGE sent to data thread to clear and update buffer
		//
		//		//clear update buffers - simulate action 1 by foreign thread after receiving
		//		//msg to update buufers
		//		drawGrpUpdate.clear(lgl2);
		//
		//		//END DEBUG
		//	}
	}
}

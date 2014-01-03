package de.grovie.renderer.GL2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.exception.GvExRendererBufferSet;
import de.grovie.exception.GvExRendererDrawGroup;
import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.exception.GvExRendererTexture2D;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.renderstate.GvRenderState;
import de.grovie.test.engine.renderer.TestRendererTex;
import de.grovie.util.file.FileResource;

public class GvIllustratorGL2  extends GvIllustrator implements GLEventListener{

	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	GLUT lglut;

	private GvRenderState lStateOverlay2D;

	public GvIllustratorGL2(GvRendererGL2 renderer)
	{
		super(renderer);

		lglAutoDrawable = null;
		lgl2 = null;

		lStateOverlay2D = new GvRenderStateGL2();
		lStateOverlay2D.lFaceCulling.lEnabled = false;
		lStateOverlay2D.lDepthTest.lEnabled = false;
		lStateOverlay2D.lTexture.lEnabled = false;
	}

	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		//triggers sequence of 3D-pipeline execution, 2D rendering and displayEnding
		//see super class GvIllustrator implementation
		display(); 
	}

	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		lglAutoDrawable = glAutoDrawable;
		lgl2 = glAutoDrawable.getGL().getGL2();
		lglu = new GLU();
		lglut = new GLUT();

		//Disable JOGL auto buffer swap to allow timing frame draw
		lglAutoDrawable.setAutoSwapBufferMode(false);

		//Max frame rate looping
		//glAutoDrawable.getGL().setSwapInterval(1); //uncomment this line for v-sync
		Animator animator = (Animator)lRenderer.getAnimator();
		animator.add(glAutoDrawable);
		animator.setRunAsFastAsPossible(true);
		animator.start();

		//init pipeline and renderer
		init();		
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		reshape(x,y,width,height);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		dispose();
	}

	@Override
	public void reshape(int x, int y, int width, int height) {
		lPipeline.reshape(x,y,width,height);
	}

	private void initPipeline() throws GvExRendererPassShaderResource
	{
		//Assign pipeline - check if multi-color attachment FBOs are supported.
		//if(lglAutoDrawable.getContext().hasFullFBOSupport())
		//	lPipeline = new GvPipelineGL2Deferred(lRenderer, lglAutoDrawable, lgl2, lglu);
		//else
		lPipeline = new GvPipelineGL2(lRenderer, lglAutoDrawable, lgl2, lglu);
	}

	private void initRenderer() throws GvExRendererVertexBuffer, GvExRendererIndexBuffer, GvExRendererVertexArray, FileNotFoundException, GvExRendererTexture2D, GvExRendererDrawGroup, GvExRendererBufferSet, GvExRendererDrawGroupRetrieval
	{
		GvRendererGL2 rendererGL2 = (GvRendererGL2)lRenderer;

		//Materials
		rendererGL2.addMaterial(new GvMaterial());

		//Textures
		//FOR DEBUG
		InputStream stream = FileResource.getResource(
				File.separator + "resources" + File.separator + "test" + File.separator + "texture" + 
						File.separator + "test.jpg");
		
		rendererGL2.addTexture2D((GvTexture2DGL2)lRenderer.getDevice().createTexture2D(stream, "jpg"));
		
		stream = FileResource.getResource(
				File.separator + "resources" + File.separator + "test" + File.separator + "texture" + 
						File.separator + "BarkDecidious0164_5_thumbhuge.jpg");
		rendererGL2.addTexture2D((GvTexture2DGL2)lRenderer.getDevice().createTexture2D(stream, "jpg"));
		//END DEBUG

		//Draw groups
		rendererGL2.initDrawGroups();

		//FOR DEBUG
		//Test update scenario
		GvDrawGroup drawGrpUpdate = rendererGL2.getDrawGroupUpdate();

		//Test geometry
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);
		int indices[] = geom.getIndices();
		float vertices[] = geom.getVertices();
		float normals[] = geom.getNormals();

		GvGeometryTex geomBoxTex = TestRendererTex.getTexturedBox();
		GvGeometryTex geomTube = TestRendererTex.getTube(1, 20, 10, 1);
		
		//send geom CPU buffers - simulate action 2 by foreign thread after receiving
		//msg to update buufers
		GvBufferSet bufferSet;
		//send geometry to categorized draw groups //TODO: discard unnecessary listing of geometry in buffer sets
		bufferSet = drawGrpUpdate.getBufferSet(false, -1, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
		bufferSet.insertGeometry(vertices, normals, indices);

		bufferSet = drawGrpUpdate.getBufferSet(true, 0, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
		bufferSet.insertGeometry(geomBoxTex.getVertices(), geomBoxTex.getNormals(), geomBoxTex.getIndices(), geomBoxTex.getUv());

		bufferSet = drawGrpUpdate.getBufferSet(true, 1, 0, GvPrimitive.PRIMITIVE_TRIANGLE_STRIP, true);
		bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices(), geomTube.getUv());
		//bufferSet.insertGeometry(geomTube.getVertices(), geomTube.getNormals(), geomTube.getIndices());
		
		lgl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
		lgl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
		
		//VBO/IBO - OpenGL server-side
		//send geom to hardware buffers - simulate action 3 by foreign thread
		//TODO: integrate this with previous geometry insertion step
		drawGrpUpdate.update(rendererGL2);

		//MESSAGE sent to rendering thread to swap buffers

		//VAOs - OpenGL client-side - 
		//rendering thread action, action 1 after receiving msg to swap VBOs
		drawGrpUpdate.updateVAO(rendererGL2);

		//VBO swap buffers - 
		//rendering thread action, action 2 after receiving msg to swap VBOs
		rendererGL2.swapBuffers();

		drawGrpUpdate = rendererGL2.getDrawGroupUpdate();
		//MESSAGE sent to data thread to clear and update buffer

		//clear update buffers - simulate action 1 by foreign thread after receiving
		//msg to update buufers
		drawGrpUpdate.clear(rendererGL2);

		//END DEBUG
	}

	@Override
	public void init()
	{
		try
		{
			initPipeline();
			initRenderer();
		}
		catch(Exception e)
		{
			System.out.println("Error initializing Renderer and Illustrator.");
		}
	}

	@Override
	public void display2DOverlay() {
		if(lRenderer.getRendererStateMachine().getOverlayOn())
		{
			lRenderer.updateRenderState(this.lStateOverlay2D, lgl2);
			
			lgl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f); //color must be set before windowPos
			
			lgl2.glWindowPos2i(5, 5);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frame time: " + lFrameTime);
			
			lgl2.glWindowPos2i(5, 15);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "FPS: " + 1.0/lFrameTime);
			
			lgl2.glWindowPos2i(5, 25);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Vertices: " + lRenderer.getIllustrator().getVertexCount());
		}
	}

	/**
	 * Invoked after display3D() and display2DOverlay()
	 */
	@Override
	public void displayEnd() {
		//swap draw buffers
		lglAutoDrawable.swapBuffers();		
	}

	@Override
	public void dispose() {
		// TODO: Call upon engine shutdown. delete allocated memory where possible.
	}

	public GLAutoDrawable getGLAutoDrawable() {
		return lglAutoDrawable;
	}

	public void setGLAutoDrawable(GLAutoDrawable glAutoDrawable) {
		this.lglAutoDrawable = glAutoDrawable;
	}

	public GL2 getGL2() {
		return lgl2;
	}

	public void setGL2(GL2 gl2) {
		this.lgl2 = gl2;
	}

	@Override
	public void processMessages() {
		// TODO check message queues and process messages

	}
}

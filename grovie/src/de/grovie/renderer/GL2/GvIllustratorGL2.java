package de.grovie.renderer.GL2;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPrimitive;

public class GvIllustratorGL2  extends GvIllustrator implements GLEventListener{
	
	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	GLUT lglut;
	
	public GvIllustratorGL2(GvRendererGL2 renderer)
	{
		super(renderer);
		
		lglAutoDrawable = null;
		lgl2 = null;
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		display();
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		lglAutoDrawable = glAutoDrawable;
		lgl2 = glAutoDrawable.getGL().getGL2();
		lglu = new GLU();
		lglut = new GLUT();
		
		//glAutoDrawable.getGL().setSwapInterval(1); //uncomment this line for v-sync
		Animator animator = (Animator)lRenderer.getAnimator();
		animator.add(glAutoDrawable);
		animator.setRunAsFastAsPossible(true);
		animator.start();
		
		try {
			init();
		} catch (GvExRendererPassShaderResource e) {
			System.out.println(e.getMessage());
		}
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

	@Override
	public void init() throws GvExRendererPassShaderResource {
		//disable JOGL auto buffer swap to allow timing frame draw
		lglAutoDrawable.setAutoSwapBufferMode(false);
		
		//check if multi-color attachment FBOs are supported. assign pipeline
		//if(lglAutoDrawable.getContext().hasFullFBOSupport())
		//	lPipeline = new GvPipelineGL2Deferred(lRenderer, lglAutoDrawable, lgl2, lglu);
		//else
			lPipeline = new GvPipelineGL2(lRenderer, lglAutoDrawable, lgl2, lglu);
			
		//FOR DEBUG
		try{
			GvRendererGL2 rendererGL2 = (GvRendererGL2)lRenderer;
			//add test material
			rendererGL2.addMaterial(new GvMaterial());
			//add test texture
			InputStream stream = new FileInputStream("/Users/yongzhiong/Downloads/test.jpg");
			rendererGL2.addTexture2D((GvTexture2DGL2)lRenderer.getDevice().createTexture2D(stream, "jpg"));
			//init draw groups
			rendererGL2.initDrawGroups();
			//create VBO/VAO/IBO
			GvDrawGroup drawGrpRender = rendererGL2.getDrawGroupRender();
			GvBufferSet bufferSet = drawGrpRender.getBufferSet(false, -1, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
			bufferSet.addArrayBuffer();
			bufferSet.addElementBuffer();
			//prepare test geom
			String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
			GvGeometry geom = new GvGeometry();
			GvImporterObj.load(path, geom);
			int indices[] = geom.getIndices();
			float vertices[] = geom.getVertices();
			float normals[] = geom.getNormals();
			//send geom to buffers
			
		}
		catch(Exception e)
		{
			System.out.println("test fail");
			System.out.println(e.getMessage());
		}
		
		//END DEBUG
	}
	
	@Override
	public void display2DOverlay() {
		if(lRenderer.getRendererStateMachine().getOverlayOn())
		{
			lgl2.glWindowPos2i(5, 5);
			lgl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frame time: " + lFrameTime);
			lgl2.glWindowPos2i(5, 15);
			lgl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "FPS: " + 1.0/lFrameTime);
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

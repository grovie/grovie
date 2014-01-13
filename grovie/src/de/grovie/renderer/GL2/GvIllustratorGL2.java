package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.renderstate.GvRenderState;

public class GvIllustratorGL2  extends GvIllustrator implements GLEventListener{

	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	GLUT lglut;

	private GvRenderState lStateOverlay2D;
	
	private GvCamera lCameraCopy;

	public GvIllustratorGL2(GvRendererGL2 renderer)
	{
		super(renderer);

		lglAutoDrawable = null;
		lgl2 = null;

		lStateOverlay2D = new GvRenderStateGL2();
		lStateOverlay2D.lFaceCulling.lEnabled = false;
		lStateOverlay2D.lDepthTest.lEnabled = false;
		lStateOverlay2D.lTexture.lEnabled = false;
		
		lCameraCopy = new GvCamera();
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
		
		lRenderer.sendCamera();
	}

	private void initPipeline() throws GvExRendererPassShaderResource
	{
		//Assign pipeline - check if multi-color attachment FBOs are supported.
		//if(lglAutoDrawable.getContext().hasFullFBOSupport())
		//	lPipeline = new GvPipelineGL2Deferred(lRenderer, lglAutoDrawable, lgl2, lglu);
		//else
		lPipeline = new GvPipelineGL2(lRenderer, lglAutoDrawable, lgl2, lglu);
	}

	@Override
	public void init()
	{
		try
		{
			//select rendering 3D pipeline 
			initPipeline();
			
			//texture mode
//			initTextureMode();
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
			
			//time per frame in seconds
			lgl2.glWindowPos2i(5, 5);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frame time: " + lFrameTime);
			
			//frames per second
			lgl2.glWindowPos2i(5, 15);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "FPS: " + 1.0/lFrameTime);
			
			//vertex count
			lgl2.glWindowPos2i(5, 25);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Vertices: " + lRenderer.getIllustrator().getVertexCount());
			
			//camera position
			lRenderer.getRendererStateMachine().getCamera(lCameraCopy);
			lgl2.glWindowPos2i(5, 35);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Camera position: (" + 
					lCameraCopy.lPosition[0] + "," +
					lCameraCopy.lPosition[1] + "," +
					lCameraCopy.lPosition[2] + ")"
					);
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
}

package de.grovie.renderer.GL2;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvPass;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvShaderProgram;
import de.grovie.renderer.GvRendererStateMachine.GvRendererState;

public class GvPassGL2 extends GvPass {

	//jogl opengl variables
	private GLAutoDrawable lglAutoDrawable;
	private GL2 lgl2;
	private GLU lglu;
	
	//container for copying camera info from renderer
	private GvCamera lCameraCopy;
	
	//textured-material-primitive shaders
	private GvShaderProgram lShaderTexMatPoint;
	private GvShaderProgram lShaderTexMatTri;
	
	//non-textured-material-primitive shaders
	private GvShaderProgram lShaderMatPoint;
	private GvShaderProgram lShaderMatTri;
	
	public GvPassGL2(GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu, GvRenderer renderer)
	{
		super(renderer);
		this.lglAutoDrawable = glAutoDrawable;
		this.lgl2 = gl2;
		this.lglu = glu;
		init();
	}
	
	@Override
	public void init() {
		//gl states
		lgl2.glDisable(GL2.GL_LIGHTING);
		lgl2.glEnable(GL2.GL_DEPTH_TEST);		
	}
	
	@Override
	public void start() {
		
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void reshape(int x, int y, int width, int height) {
		lgl2.glViewport(0, 0, width, height);
		float aspectRatio = (float)width / (float)height;

		//set aspect ratio into camera instance
		if(lRenderer.getRendererStateMachine().setState(
				GvRendererState.CAMERA_ASPECT_CHANGE)
				)
		{
			lRenderer.getRendererStateMachine().cameraSetAspect(aspectRatio);
			lRenderer.getRendererStateMachine().setState(GvRendererState.IDLE);
		}
		
		lRenderer.getRendererStateMachine().getCamera(lCameraCopy);
		
		/* Setup the view of the cube. */
		lgl2.glMatrixMode(GL2.GL_PROJECTION);
		lgl2.glLoadIdentity();
		lglu.gluPerspective( lCameraCopy.lFov,
				lCameraCopy.lAspect,
				lCameraCopy.lNear,
				lCameraCopy.lFar	
				);
	}
}

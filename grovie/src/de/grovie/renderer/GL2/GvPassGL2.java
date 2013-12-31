package de.grovie.renderer.GL2;

import java.io.File;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import de.grovie.exception.GvExceptionRendererPassShaderResource;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvPass;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvRendererStateMachine.GvRendererState;
import de.grovie.renderer.GvShaderProgram;
import de.grovie.util.file.FileResource;

public class GvPassGL2 extends GvPass {

	//jogl opengl variables
	private GLAutoDrawable lglAutoDrawable;
	private GL2 lgl2;
	private GLU lglu;

	//container for copying camera info from renderer
	private GvCamera lCameraCopy;

	//non-textured-material-primitive shaders
	private GvShaderProgram lShaderMatPoint;
	private GvShaderProgram lShaderMatTri;

	//textured-material-primitive shaders
	private GvShaderProgram lShaderTexMatPoint;
	private GvShaderProgram lShaderTexMatTri;
	
	

	public GvPassGL2(GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu, GvRenderer renderer) throws GvExceptionRendererPassShaderResource
	{
		super(renderer);
		this.lglAutoDrawable = glAutoDrawable;
		this.lgl2 = gl2;
		this.lglu = glu;
		lCameraCopy = new GvCamera();
		init();
	}

	@Override
	public void init() throws GvExceptionRendererPassShaderResource {
		//gl states
		lgl2.glDisable(GL2.GL_LIGHTING);
		lgl2.glEnable(GL2.GL_DEPTH_TEST);

		try {
			String srcMaterialPointF = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialPointF.glsl");
			String srcMaterialPointV = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialPointV.glsl");
			
//			String srcMaterialTriangleF = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "MaterialTriangleF.glsl");
//			String srcMaterialTriangleV = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "MaterialTriangleV.glsl");
//
//			String srcTextureMaterialPointF = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "TextureMaterialPointF.glsl");
//			String srcTextureMaterialPointV = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "TextureMaterialPointV.glsl");
//			String srcTextureMaterialTriangleF = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "TextureMaterialTriangleF.glsl");
//			String srcTextureMaterialTriangleV = FileResource.getResourceAsString(
//					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
//							File.separator + "TextureMaterialTriangleV.glsl");
			
			GvDevice device = lRenderer.getDevice();
			lShaderMatPoint = device.createShaderProgram(srcMaterialPointV, srcMaterialPointF);
//			lShaderMatTri = device.createShaderProgram(srcMaterialTriangleV, srcMaterialTriangleF);
//			lShaderTexMatPoint = device.createShaderProgram(srcTextureMaterialPointV, srcTextureMaterialPointF);
//			lShaderTexMatTri = device.createShaderProgram(srcTextureMaterialTriangleV, srcTextureMaterialTriangleF);

		} catch (Exception e) {
			throw new GvExceptionRendererPassShaderResource("Error loading shader source");
		}
	}

	@Override
	public void start() {
		
		//clear frame buffer
		lgl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		//get camera info
		lRenderer.getRendererStateMachine().getCamera(lCameraCopy);
		
		//set model-view matrix
		lgl2.glMatrixMode(GL2.GL_MODELVIEW);
		lgl2.glLoadIdentity();
		lglu.gluLookAt(lCameraCopy.lPosition[0],lCameraCopy.lPosition[1],lCameraCopy.lPosition[2],
				lCameraCopy.lPosition[0]+lCameraCopy.lView[0],
				lCameraCopy.lPosition[1]+lCameraCopy.lView[1],
				lCameraCopy.lPosition[2]+lCameraCopy.lView[2],
				lCameraCopy.lUp[0], lCameraCopy.lUp[1],lCameraCopy.lUp[2]);

	}

	@Override
	public void execute() {
		GvRendererGL2 renderer = (GvRendererGL2)lRenderer;
		int shaderId;
		
		shaderId = lShaderMatPoint.getId();
		lgl2.glUseProgram(shaderId);
		renderer.drawPoints(shaderId);
		
//		shaderId = lShaderMatTri.getId();
//		lgl2.glUseProgram(shaderId);
//		renderer.drawTriangles(shaderId);
//		
//		shaderId = lShaderTexMatPoint.getId();
//		lgl2.glUseProgram(shaderId);
//		renderer.drawTexturePoints(shaderId);
//		
//		shaderId = lShaderTexMatTri.getId();
//		lgl2.glUseProgram(shaderId);
//		renderer.drawTextureTriangles(shaderId);	
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

		//get camera info
		lRenderer.getRendererStateMachine().getCamera(lCameraCopy);

		//update projection matrix
		lgl2.glMatrixMode(GL2.GL_PROJECTION);
		lgl2.glLoadIdentity();
		lglu.gluPerspective( lCameraCopy.lFov,
				lCameraCopy.lAspect,
				lCameraCopy.lNear,
				lCameraCopy.lFar	
				);
	}
}

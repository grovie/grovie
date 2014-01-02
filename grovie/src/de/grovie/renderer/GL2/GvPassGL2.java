package de.grovie.renderer.GL2;

import java.io.File;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererPassPrimitiveTypeUnknown;
import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPass;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvRendererStateMachine.GvRendererState;
import de.grovie.renderer.GvShaderProgram;
import de.grovie.renderer.GvVertexArray;
import de.grovie.renderer.renderstate.GvRenderState;
import de.grovie.util.file.FileResource;

/**
 * This class emulates the OpenGL fixed pipeline using VBOs/VAOs as well as custom shaders.
 * 
 * @author yong
 *
 */
public class GvPassGL2 extends GvPass {

	//jogl opengl variables
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
	
	//render states
	private GvRenderState lStateCullEnabled;
	private GvRenderState lStateCullDisabled;

	public GvPassGL2(GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu, GvRenderer renderer) throws GvExRendererPassShaderResource
	{
		super(renderer);
		this.lgl2 = gl2;
		this.lglu = glu;
		lCameraCopy = new GvCamera();
		
		init();
	}

	/**
	 * Initialize pass.
	 * Creates required render states and shaders
	 */
	@Override
	public void init() throws GvExRendererPassShaderResource {
		
		initRenderStates();

		try {
			String srcMaterialPointF = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialPointF.glsl");
			String srcMaterialPointV = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialPointV.glsl");
			
			String srcMaterialTriangleF = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialTriangleF.glsl");
			String srcMaterialTriangleV = FileResource.getResourceAsString(
					File.separator + "resources" + File.separator + "shader" + File.separator + "gl2" + File.separator + "standard" +
							File.separator + "MaterialTriangleV.glsl");

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
			lShaderMatTri = device.createShaderProgram(srcMaterialTriangleV, srcMaterialTriangleF);
//			lShaderTexMatPoint = device.createShaderProgram(srcTextureMaterialPointV, srcTextureMaterialPointF);
//			lShaderTexMatTri = device.createShaderProgram(srcTextureMaterialTriangleV, srcTextureMaterialTriangleF);

		} catch (Exception e) {
			throw new GvExRendererPassShaderResource("Error loading shader source");
		}
	}

	/**
	 * Starts the rendering pass.
	 * Clear drawing target, set model-view matrix
	 */
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
	public void execute() throws GvExRendererDrawGroupRetrieval, GvExRendererPassPrimitiveTypeUnknown {
		GvRendererGL2 renderer = (GvRendererGL2)lRenderer;

		GvDrawGroup drawGroup = renderer.getDrawGroupRender();
		
		ArrayList<GvMaterial> materials = renderer.getMaterials();
		
		for(int i=0; i<materials.size(); ++i)
		{
			for(int j=0; j<GvPrimitive.PRIMITIVE_COUNT; ++j)
			{
				GvBufferSetGL2 bufferSet = (GvBufferSetGL2)drawGroup.getBufferSet(false, -1, i, j, true);
				ArrayList<GvVertexArray> vaos = bufferSet.getVertexArrays();
				int vaoCount = vaos.size();
				if(vaoCount > 0)
				{
					renderer.updateRenderState(lStateCullEnabled, lgl2);
					
					GvShaderProgram program;
					int glPrimitiveType;
					if(j==GvPrimitive.PRIMITIVE_POINT)
					{
						program = lShaderMatPoint;
						glPrimitiveType = GL2.GL_POINTS;
					}
					else if(j==GvPrimitive.PRIMITIVE_TRIANGLE)
					{
						program = lShaderMatTri;
						glPrimitiveType = GL2.GL_TRIANGLES;
					}
					else if(j==GvPrimitive.PRIMITIVE_TRIANGLE_STRIP)
					{
						program = lShaderMatTri;
						glPrimitiveType = GL2.GL_TRIANGLE_STRIP;
					}
					else
						throw new GvExRendererPassPrimitiveTypeUnknown("Unknown primitive type");
					
					GL2 gl2 = ((GvIllustratorGL2)renderer.getIllustrator()).getGL2();
					int shaderProgramId = program.getId();
					gl2.glUseProgram(shaderProgramId);
					
					//1. lightDir - world space - directional light - direction from vertex to light source
					int idLightDir = gl2.glGetUniformLocation(shaderProgramId,"lightDir");
					gl2.glUniform3f(idLightDir,0.5773502f,0.5773502f,0.5773502f);

					//2. light ambient,diffuse,specular
					int idLightAmbi = gl2.glGetUniformLocation(shaderProgramId,"lightAmb");
					int idLightDiff = gl2.glGetUniformLocation(shaderProgramId,"lightDif");
					int idLightSpec = gl2.glGetUniformLocation(shaderProgramId,"lightSpe");
					gl2.glUniform4f(idLightAmbi,1.0f,1.0f,1.0f,1.0f);
					gl2.glUniform4f(idLightDiff,1.0f,1.0f,1.0f,1.0f);
					gl2.glUniform4f(idLightSpec,1.0f,1.0f,1.0f,1.0f);

					//3. material ambient,diffuse,specular,shininess
					GvMaterial material = materials.get(i);
					int idMaterialAmbi = gl2.glGetUniformLocation(shaderProgramId,"materialAmb");
					int idMaterialDiff = gl2.glGetUniformLocation(shaderProgramId,"materialDif");
					int idMaterialSpec = gl2.glGetUniformLocation(shaderProgramId,"materialSpe");
					int idMaterialShin = gl2.glGetUniformLocation(shaderProgramId,"materialShi");
					gl2.glUniform4f(idMaterialAmbi,material.lAmbient[0],material.lAmbient[1],material.lAmbient[2],material.lAmbient[3]);
					gl2.glUniform4f(idMaterialDiff,material.lDiffuse[0],material.lDiffuse[1],material.lDiffuse[2],material.lDiffuse[3]);
					gl2.glUniform4f(idMaterialSpec,material.lSpecular[0],material.lSpecular[1],material.lSpecular[2],material.lSpecular[3]);
					gl2.glUniform1f(idMaterialShin, material.lShininess);

					//4. global ambient
					int idGlobalAmbi = gl2.glGetUniformLocation(shaderProgramId,"globalAmbi");
					gl2.glUniform4f(idGlobalAmbi,0.1f,0.1f,0.1f,1.0f);

					//5. camera position
					int idCameraPos = gl2.glGetUniformLocation(shaderProgramId,"cameraPos");
					gl2.glUniform3f(idCameraPos,lCameraCopy.lPosition[0],lCameraCopy.lPosition[1],lCameraCopy.lPosition[2]);
					
					for(int k=0; k<vaoCount; ++k)
					{
						GvVertexArray vao = vaos.get(k);
						
						gl2.glBindVertexArray(vao.getId());
						
						gl2.glDrawElements(
								glPrimitiveType,      		// mode
								vao.getSizeIndices()/4,    	// count
								GL2.GL_UNSIGNED_INT,   		// type
								vao.getIboOffset()          // element array buffer offset
								);
						
						gl2.glBindVertexArray(0);
					}
				}
			}
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/**
	 * Sets the viewport, camera aspect-ratio and projection matrix
	 */
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
	
	/**
	 * Initializes rendering states
	 */
	private void initRenderStates()
	{
		lStateCullEnabled = new GvRenderStateGL2();
		lStateCullEnabled.lFaceCulling.lEnabled = true;
		lStateCullEnabled.lLighting.lEnabled = false;
		lStateCullEnabled.lDepthTest.lEnabled = true;
		
		lStateCullDisabled = new GvRenderStateGL2();
		lStateCullDisabled.lFaceCulling.lEnabled = false;
		lStateCullDisabled.lLighting.lEnabled = false;
		lStateCullEnabled.lDepthTest.lEnabled = true;
	}
}

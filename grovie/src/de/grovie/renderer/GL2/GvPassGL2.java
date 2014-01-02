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
import de.grovie.renderer.GvLight;
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
	
	//container for copying light info from renderer
	private GvLight lLightCopy;

	//container for selecting primitive type and shader program
	private class GvShaderPrimitiveWrapper
	{
		int lGLShader;		//OpenGL shader program id
		int lGLPrimitive;	//OpenGL primitive type constant
	}
	private GvShaderPrimitiveWrapper lShaderPrimitiveWrapper;
	
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
		lLightCopy = new GvLight();
		lShaderPrimitiveWrapper = new GvShaderPrimitiveWrapper();
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
		
		//get reference to group of geometry for rendering
		GvDrawGroup drawGroup = renderer.getDrawGroupRender();
		
		//get list of materials in scene
		ArrayList<GvMaterial> materials = renderer.getMaterials();
		
		//get number of lights in scene
		int lightCount = renderer.getRendererStateMachine().getLightCount();
		
		//render non-textured geometry
		for(int materialIndex=0; materialIndex<materials.size(); ++materialIndex)
		{
			for(int primitiveIndex=0; primitiveIndex<GvPrimitive.PRIMITIVE_COUNT; ++primitiveIndex)
			{
				//render backface-culled geometry
				GvBufferSetGL2 bufferSet = (GvBufferSetGL2)drawGroup.getBufferSet(
						false,			//non-textured
						-1,				//non-textured
						materialIndex,
						primitiveIndex,
						true); 			//culled geometry
				executeBufferSet(bufferSet, renderer, materials.get(materialIndex), primitiveIndex, lightCount,true);
				
				//render non-backface-culled geometry
				bufferSet = (GvBufferSetGL2)drawGroup.getBufferSet(
						false,			//non-textured
						-1,				//non-textured
						materialIndex,
						primitiveIndex,
						false); 		//un-culled geometry
				executeBufferSet(bufferSet, renderer, materials.get(materialIndex), primitiveIndex, lightCount,false);
			}
		}
		
		//render textured geometry
		
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
	
	private void glslMaterial(GvMaterial material, GL2 gl2, int shaderProgramId)
	{
		int idMaterialAmbi = gl2.glGetUniformLocation(shaderProgramId,"materialAmb");
		int idMaterialDiff = gl2.glGetUniformLocation(shaderProgramId,"materialDif");
		int idMaterialSpec = gl2.glGetUniformLocation(shaderProgramId,"materialSpe");
		int idMaterialShin = gl2.glGetUniformLocation(shaderProgramId,"materialShi");
		gl2.glUniform4f(idMaterialAmbi,material.lAmbient[0],material.lAmbient[1],material.lAmbient[2],material.lAmbient[3]);
		gl2.glUniform4f(idMaterialDiff,material.lDiffuse[0],material.lDiffuse[1],material.lDiffuse[2],material.lDiffuse[3]);
		gl2.glUniform4f(idMaterialSpec,material.lSpecular[0],material.lSpecular[1],material.lSpecular[2],material.lSpecular[3]);
		gl2.glUniform1f(idMaterialShin, material.lShininess);
	}
	
	private void glslLights(int lightCount, GL2 gl2, int shaderProgramId)
	{
		int idLightCount = gl2.glGetUniformLocation(shaderProgramId,"lightCount");
		gl2.glUniform1i(idLightCount, lightCount);
		
		int idLightDir,idLightAmb, idLightDif, idLightSpe;
		for(int lightIndex=0; lightIndex<lightCount; lightIndex++)
		{
			lRenderer.getRendererStateMachine().getLight(lightIndex, lLightCopy);
			
			idLightDir = gl2.glGetUniformLocation(shaderProgramId,"lights["+lightIndex+"].lightDir");
			idLightAmb = gl2.glGetUniformLocation(shaderProgramId,"lights["+lightIndex+"].lightAmb");
			idLightDif = gl2.glGetUniformLocation(shaderProgramId,"lights["+lightIndex+"].lightDif");
			idLightSpe = gl2.glGetUniformLocation(shaderProgramId,"lights["+lightIndex+"].lightSpe");
			
			gl2.glUniform3f(idLightDir,lLightCopy.lPosition[0],lLightCopy.lPosition[1],lLightCopy.lPosition[2]);
			gl2.glUniform4f(idLightAmb,lLightCopy.lAmbient[0],lLightCopy.lAmbient[1],lLightCopy.lAmbient[2],lLightCopy.lAmbient[3]);
			gl2.glUniform4f(idLightDif,lLightCopy.lDiffuse[0],lLightCopy.lDiffuse[1],lLightCopy.lDiffuse[2],lLightCopy.lDiffuse[3]);
			gl2.glUniform4f(idLightSpe,lLightCopy.lSpecular[0],lLightCopy.lSpecular[1],lLightCopy.lSpecular[2],lLightCopy.lSpecular[3]);
		}
	}
	
	private void selectProgramAndGLPrimitive(int primitiveType) throws GvExRendererPassPrimitiveTypeUnknown
	{
		if(primitiveType==GvPrimitive.PRIMITIVE_POINT)
		{
			lShaderPrimitiveWrapper.lGLShader = lShaderMatPoint.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_POINTS;
		}
		else if(primitiveType==GvPrimitive.PRIMITIVE_TRIANGLE)
		{
			lShaderPrimitiveWrapper.lGLShader = lShaderMatTri.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_TRIANGLES;
		}
		else if(primitiveType==GvPrimitive.PRIMITIVE_TRIANGLE_STRIP)
		{
			lShaderPrimitiveWrapper.lGLShader = lShaderMatTri.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_TRIANGLE_STRIP;
		}
		else
			throw new GvExRendererPassPrimitiveTypeUnknown("Unknown primitive type");
		
	}
	
	private void executeBufferSet(GvBufferSetGL2 bufferSet, GvRendererGL2 renderer, GvMaterial material, int primitiveIndex, int lightCount, boolean culled) throws GvExRendererPassPrimitiveTypeUnknown
	{
		ArrayList<GvVertexArray> vaos = bufferSet.getVertexArrays();
		int vaoCount = vaos.size();
		if(vaoCount > 0)
		{
			if(culled)
				renderer.updateRenderState(lStateCullEnabled, lgl2);
			else
				renderer.updateRenderState(lStateCullDisabled, lgl2);
			
			//select correct GL primitive type constant and shader program
			selectProgramAndGLPrimitive(primitiveIndex);
			
			//bind and use shader program
			GL2 gl2 = ((GvIllustratorGL2)renderer.getIllustrator()).getGL2();
			int shaderProgramId = lShaderPrimitiveWrapper.lGLShader;
			gl2.glUseProgram(shaderProgramId);
			
			//GLSL variables 1. Lights
			glslLights(lightCount, gl2, shaderProgramId);
			
			//GLSL variables 2. material ambient,diffuse,specular,shininess
			glslMaterial(material, gl2, shaderProgramId);

			//GLSL variables 3. global ambient
			int idGlobalAmbi = gl2.glGetUniformLocation(shaderProgramId,"globalAmbi");
			gl2.glUniform4f(idGlobalAmbi,0.1f,0.1f,0.1f,1.0f);

			//GLSL variables 4. camera position
			int idCameraPos = gl2.glGetUniformLocation(shaderProgramId,"cameraPos");
			gl2.glUniform3f(idCameraPos,lCameraCopy.lPosition[0],lCameraCopy.lPosition[1],lCameraCopy.lPosition[2]);
			
			//Draw geometry with VAOs
			for(int k=0; k<vaoCount; ++k)
			{
				GvVertexArray vao = vaos.get(k);
				
				gl2.glBindVertexArray(vao.getId());
				
				gl2.glDrawElements(
						lShaderPrimitiveWrapper.lGLPrimitive, // mode
						vao.getSizeIndices()/4,    	// count
						GL2.GL_UNSIGNED_INT,   		// type
						vao.getIboOffset()          // element array buffer offset
						);
				
				gl2.glBindVertexArray(0);
			}
		}
	}
}

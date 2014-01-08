package de.grovie.renderer.GL2;

import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.texture.Texture;

import de.grovie.exception.GvExRendererDrawGroupRetrieval;
import de.grovie.exception.GvExRendererPassPrimitiveTypeUnknown;
import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvLight;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPass;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvRendererStateMachine.GvRendererState;
import de.grovie.renderer.GvShaderProgram;
import de.grovie.renderer.GvVertexArray;
import de.grovie.renderer.renderstate.GvRenderState;
import de.grovie.renderer.shader.gl2.standard.GvMaterialPointF;
import de.grovie.renderer.shader.gl2.standard.GvMaterialPointV;
import de.grovie.renderer.shader.gl2.standard.GvMaterialTriangleF;
import de.grovie.renderer.shader.gl2.standard.GvMaterialTriangleV;
import de.grovie.renderer.shader.gl2.standard.GvTextureMaterialPointF;
import de.grovie.renderer.shader.gl2.standard.GvTextureMaterialPointV;
import de.grovie.renderer.shader.gl2.standard.GvTextureMaterialTriangleF;
import de.grovie.renderer.shader.gl2.standard.GvTextureMaterialTriangleV;

/**
 * This class emulates the OpenGL fixed pipeline using VBOs/VAOs and custom shaders.
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
	
	//vertex counter
	private long lVertexCount;

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
	private GvRenderState lStateCullEnabledTexDisabled;
	private GvRenderState lStateCullDisabledTexDisabled;
	private GvRenderState lStateCullEnabledTexEnabled;
	private GvRenderState lStateCullDisabledTexEnabled;

	public GvPassGL2(GLAutoDrawable glAutoDrawable, GL2 gl2, GLU glu, GvRenderer renderer) throws GvExRendererPassShaderResource
	{
		super(renderer);
		this.lgl2 = gl2;
		this.lglu = glu;
		lCameraCopy = new GvCamera();
		lLightCopy = new GvLight();
		lShaderPrimitiveWrapper = new GvShaderPrimitiveWrapper();
		lVertexCount = 0;
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
			String srcMaterialPointF = GvMaterialPointF.kSource;
			String srcMaterialPointV = GvMaterialPointV.kSource;
			String srcMaterialTriangleF = GvMaterialTriangleF.kSource;
			String srcMaterialTriangleV = GvMaterialTriangleV.kSource;
			String srcTextureMaterialPointF = GvTextureMaterialPointF.kSource;
			String srcTextureMaterialPointV = GvTextureMaterialPointV.kSource;
			String srcTextureMaterialTriangleF = GvTextureMaterialTriangleF.kSource;
			String srcTextureMaterialTriangleV = GvTextureMaterialTriangleV.kSource;
			
			GvDevice device = lRenderer.getDevice();
			lShaderMatPoint = device.createShaderProgram(srcMaterialPointV, srcMaterialPointF,lgl2);
			lShaderMatTri = device.createShaderProgram(srcMaterialTriangleV, srcMaterialTriangleF,lgl2);
			lShaderTexMatPoint = device.createShaderProgram(srcTextureMaterialPointV, srcTextureMaterialPointF,lgl2);
			lShaderTexMatTri = device.createShaderProgram(srcTextureMaterialTriangleV, srcTextureMaterialTriangleF,lgl2);

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
		
		//reset vertex counter
		lVertexCount = 0;
		
		//get reference to group of geometry for rendering
		GvDrawGroup drawGroup = renderer.getDrawGroupRender();
		
		//check if draw groups have been initialized or are empty
		if(drawGroup.isEmpty())
			return;
		
		//get list of materials in scene
		ArrayList<GvMaterial> materials = renderer.getMaterials();
		
		//get list of textures in scene
		ArrayList<GvTexture2DGL2> textures = renderer.getTextures();
		
		//get number of lights in scene
		int lightCount = renderer.getRendererStateMachine().getLightCount();
		
		//render non-textured geometry
		traverseGroupsMaterialsPrimitives(materials, drawGroup, renderer, lightCount,false,-1);
		//render textured geometry
		traverseGroupsTextures(textures, materials, drawGroup, renderer, lightCount);
			
		//update vertex count
		GvIllustrator illustrator = renderer.getIllustrator();
		illustrator.setVertexCount(lVertexCount);
	}

	@Override
	public void stop() {
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
		lStateCullEnabledTexDisabled = new GvRenderStateGL2();
		lStateCullEnabledTexDisabled.lFaceCulling.lEnabled = true;
		lStateCullEnabledTexDisabled.lDepthTest.lEnabled = true;
		lStateCullEnabledTexDisabled.lTexture.lEnabled = false;
		
		lStateCullDisabledTexDisabled = new GvRenderStateGL2();
		lStateCullDisabledTexDisabled.lFaceCulling.lEnabled = false;
		lStateCullDisabledTexDisabled.lDepthTest.lEnabled = true;
		lStateCullDisabledTexDisabled.lTexture.lEnabled = false;
		
		lStateCullEnabledTexEnabled = new GvRenderStateGL2();
		lStateCullEnabledTexEnabled.lFaceCulling.lEnabled = true;
		lStateCullEnabledTexEnabled.lDepthTest.lEnabled = true;
		lStateCullEnabledTexEnabled.lTexture.lEnabled = true;
		
		lStateCullDisabledTexEnabled = new GvRenderStateGL2();
		lStateCullDisabledTexEnabled.lFaceCulling.lEnabled = false;
		lStateCullDisabledTexEnabled.lDepthTest.lEnabled = true;
		lStateCullDisabledTexEnabled.lTexture.lEnabled = true;
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
	
	private void selectProgramAndGLPrimitive(int primitiveType, boolean textured) throws GvExRendererPassPrimitiveTypeUnknown
	{
		switch(primitiveType)
		{
		case GvPrimitive.PRIMITIVE_POINT:
			lShaderPrimitiveWrapper.lGLShader = textured?lShaderTexMatPoint.getId():lShaderMatPoint.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_POINTS;
			break;
		case GvPrimitive.PRIMITIVE_TRIANGLE:
			lShaderPrimitiveWrapper.lGLShader = textured?lShaderTexMatTri.getId():lShaderMatTri.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_TRIANGLES;
			break;
		case GvPrimitive.PRIMITIVE_TRIANGLE_STRIP:
			lShaderPrimitiveWrapper.lGLShader = textured?lShaderTexMatTri.getId():lShaderMatTri.getId();
			lShaderPrimitiveWrapper.lGLPrimitive = GL2.GL_TRIANGLE_STRIP;
			break;
		default:
			throw new GvExRendererPassPrimitiveTypeUnknown("Unknown primitive type");
		}
	}

	private void executeBufferSet(GvBufferSetGL2 bufferSet, GvRendererGL2 renderer, GvMaterial material, int primitiveIndex, int lightCount, boolean culled, boolean textured) throws GvExRendererPassPrimitiveTypeUnknown
	{
		ArrayList<GvVertexArray> vaos = bufferSet.getVertexArrays();
		int vaoCount = vaos.size();
		if(vaoCount > 0)
		{
			if(culled && textured)
				renderer.updateRenderState(lStateCullEnabledTexEnabled, lgl2);
			else if (culled && !textured)
				renderer.updateRenderState(lStateCullEnabledTexDisabled, lgl2);
			else if(!culled && !textured)
				renderer.updateRenderState(lStateCullDisabledTexDisabled, lgl2);
			else if(!culled && textured)
				renderer.updateRenderState(lStateCullDisabledTexEnabled, lgl2);
			
			//select correct GL primitive type constant and shader program
			selectProgramAndGLPrimitive(primitiveIndex,textured);
			
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
				
				lVertexCount += vao.getSizeVertices();
			}
			
			gl2.glUseProgram(0);
		}
	}
	
	/**
	 * Loops through draw groups categorized by material and primitive types. Render the
	 * geometry within.
	 * @param materials
	 * @param drawGroup
	 * @param renderer
	 * @param lightCount
	 * @param textured
	 * @param textureIndex
	 * @throws GvExRendererDrawGroupRetrieval
	 * @throws GvExRendererPassPrimitiveTypeUnknown
	 */
	private void traverseGroupsMaterialsPrimitives(ArrayList<GvMaterial> materials, GvDrawGroup drawGroup, GvRendererGL2 renderer, int lightCount, boolean textured, int textureIndex) throws GvExRendererDrawGroupRetrieval, GvExRendererPassPrimitiveTypeUnknown
	{
		for(int materialIndex=0; materialIndex<materials.size(); ++materialIndex)
		{
			GvMaterial material = materials.get(materialIndex);
			
			for(int primitiveIndex=0; primitiveIndex<GvPrimitive.PRIMITIVE_COUNT; ++primitiveIndex)
			{
				//render backface-culled geometry
				GvBufferSetGL2 bufferSet = (GvBufferSetGL2)drawGroup.getBufferSet(
						textured,			//non-textured
						textureIndex,				//non-textured
						materialIndex,
						primitiveIndex,
						true); 			//culled geometry
				executeBufferSet(bufferSet, renderer, material, primitiveIndex, lightCount,true,textured);
				
				//render non-backface-culled geometry
				bufferSet = (GvBufferSetGL2)drawGroup.getBufferSet(
						textured,			//non-textured
						textureIndex,				//non-textured
						materialIndex,
						primitiveIndex,
						false); 		//un-culled geometry
				executeBufferSet(bufferSet, renderer, material, primitiveIndex, lightCount,false,textured);
			}
		}
	}
	
	/**
	 * Loops through the textured draw groups and render geometry contained within.
	 * @param textures
	 * @param materials
	 * @param drawGroup
	 * @param renderer
	 * @param lightCount
	 * @throws GvExRendererDrawGroupRetrieval
	 * @throws GvExRendererPassPrimitiveTypeUnknown
	 */
	private void traverseGroupsTextures(ArrayList<GvTexture2DGL2> textures, ArrayList<GvMaterial> materials, GvDrawGroup drawGroup, GvRendererGL2 renderer, int lightCount) throws GvExRendererDrawGroupRetrieval, GvExRendererPassPrimitiveTypeUnknown
	{
		for(int textureIndex=0; textureIndex<textures.size(); ++textureIndex)
		{
			Texture texture = textures.get(textureIndex).getTexture();
			texture.enable(lgl2);
			texture.bind(lgl2);
			
			traverseGroupsMaterialsPrimitives(materials, drawGroup, renderer, lightCount, true, textureIndex);
		}
	}
}

package de.grovie.test.engine.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvLight;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvRendererStateMachine;
import de.grovie.renderer.GL2.GvRendererGL2;
import de.grovie.renderer.GvRendererStateMachine.GvRendererState;
import de.grovie.renderer.windowsystem.AWT.GvWindowSystemAWTGL;

public class TestRendererDeferred {

	//camera - temporary variable for copying camera info from renderer state machine
	static GvCamera cameraInstance = new GvCamera();

	//light - temporary variable for copying light info from renderer state machine
	static GvLight lightInstance = new GvLight();

	//view, projection 
	//static float[] matProjection = new float[16];	//projection matrix obtained from obtained from openGL in pass 1
	static float[] matView = new float[16];		//view matrix obtained from obtained from openGL in pass 1
	static float[] matViewInv = new float[16];	//inverse of view matrix
	static float[] eyeSpaceBound = new float[2];//right and top bounds of view frustum

	//geometry
	public static float vertices[]; //vertices
	public static float normals[]; 	//normals
	public static int indices[];	//vertex indices

	//OpenGL VBO and index buffer object identifiers
	public static int[] vboId; //vbo Id
	public static int[] iboId; //index array object id
	//OpenGL utility classes
	static GLU glu;
	static GLUT glut;

	//deferred pipeline - pass 1 - gBuffer
	static final int gBufferTargetCount = 3;
	static int[] gBufferProgram;
	static int[] gBufferShaderV;
	static int[] gBufferShaderF;
	static int[] gBufferTgtsRender;
	static int[] gBufferTgtsTexture;
	static int[] gBufferFbo;

	//deferred pipeline - pass 2 - light
	static final int lightTargetCount = 2;
	static int[] lightAProgram;
	static int[] lightAShaderV;
	static int[] lightAShaderF;
	static int[] lightBProgram;
	static int[] lightBShaderV;
	static int[] lightBShaderF;
	static int[] lightATgtsRender;
	static int[] lightATgtsTexture;
	static int[] lightAFbo;
	static int[] lightBTgtsRender;
	static int[] lightBTgtsTexture;
	static int[] lightBFbo;
	static int lightAPrevDiff;
	static int lightAPrevSpec;
	static int lightBPrevDiff;
	static int lightBPrevSpec;
	static boolean lightShaderToggle;

	//deferred pipeline - pass 3 - color
	static final int colorTargetCount = 1;
	static int[] colorProgram;
	static int[] colorShaderV;
	static int[] colorShaderF;
	static int[] colorTgtsRender;
	static int[] colorTgtsTexture;
	static int[] colorFbo;



	public static void main(String[] args) {
		//create windowing system - Java AWT
		GvWindowSystemAWTGL windowSystem = new GvWindowSystemAWTGL();

		//create renderer to use - OpenGL 3x
//		GvRendererGL2 gvRenderer = new GvRendererGL2(
//				windowSystem,
//				"Test VBO",
//				640,
//				480);

		//test draw obj file
		initObj();

		//gvRenderer.start();

	}

	private static void initObj() {
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\teapot\\teapot2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\dragon\\dragon2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\sponza.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\teapot\\teapot2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\dragon\\dragon2.obj";
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		//String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);

		indices = geom.getIndices();
		vertices = geom.getVertices();
		normals = geom.getNormals();
		System.out.println("Polygon count: " + indices.length/3);
	}

	public static void init(GL2 gl2, GvRenderer renderer) {
		initGL(gl2,renderer);
		initShaders(gl2, renderer);
		initVBOs(gl2);
	}

	private static void initShaders(GL2 gl2, GvRenderer renderer) {
		gBufferProgram = new int[1];
		gBufferShaderV = new int[1];
		gBufferShaderF = new int[1];
		gBufferTgtsRender  = new int[gBufferTargetCount];
		gBufferTgtsTexture = new int[gBufferTargetCount];
		gBufferFbo = new int[1];

		lightAProgram = new int[1];
		lightAShaderV = new int[1];
		lightAShaderF = new int[1];
		lightATgtsRender  = new int[lightTargetCount];
		lightATgtsTexture = new int[lightTargetCount];
		lightAFbo = new int[1];

		lightBProgram = new int[1];
		lightBShaderV = new int[1];
		lightBShaderF = new int[1];
		lightBTgtsRender  = new int[lightTargetCount];
		lightBTgtsTexture = new int[lightTargetCount];
		lightBFbo = new int[1];

		colorProgram = new int[1];
		colorShaderV = new int[1];
		colorShaderF = new int[1];
		colorTgtsRender  = new int[colorTargetCount];
		colorTgtsTexture = new int[colorTargetCount];
		colorFbo = new int[1];
	}

	private static void initGL(GL2 gl2, GvRenderer renderer) {
		glu = new GLU();
		glut = new GLUT();
	}

	private static void initVBOs(GL2 gl2) {

		//generate VBO
		vboId = new int[1];
		IntBuffer vboIdBuffer = IntBuffer.wrap(vboId);
		gl2.glGenBuffers(1, vboIdBuffer);

		//bind VBO
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId[0]);

		//set total size of buffer (allocate)
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, //type of buffer
				(vertices.length * 4)+(normals.length * 4), //size in bytes of buffer
				null, //no data to be copied into VBO at this moment
				GL2.GL_STATIC_DRAW //buffer usage hint
				);

		//copy vertex data into VBO
		FloatBuffer verticesBuffer = FloatBuffer.wrap(vertices);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices.length*4, verticesBuffer);

		//copy normals data into VBO
		FloatBuffer normalsBuffer = FloatBuffer.wrap(normals);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vertices.length*4, normals.length*4, normalsBuffer);

		//generate Index buffer
		iboId = new int[1];
		IntBuffer iboIdBuffer = IntBuffer.wrap(iboId);
		gl2.glGenBuffers(1, iboIdBuffer);

		//bind IBO
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);

		//set total size of buffer (allocate) and copy indices into it
		IntBuffer indicesBuffer = IntBuffer.wrap(indices);
		gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, //type of buffer
				(indices.length * 4), //size in bytes of buffer
				indicesBuffer, //no data to be copied into VBO at this moment
				GL2.GL_STATIC_DRAW //buffer usage hint
				);


	}

	public static void render(GL2 gl2, int width, int height,
			GvRenderer renderer) {

		//1. deferred shading pipeline - pass 1 - gbuffer
		gBufferStart(gl2,renderer);
		gl2.glColor4f(1.0f,0,0,1.0f);
		drawObjVBO(gl2);
		gBufferStop(gl2);

		//drawTexture(gBufferTgtsTexture[0], gl2,renderer);	//FOR DEBUG - see normal buffer
		//drawTexture(gBufferTgtsTexture[1], gl2,renderer);	//FOR DEBUG - see color buffer
		//drawTexture(gBufferTgtsTexture[2], gl2,renderer); //FOR DEBUG - see depth buffer

		//2. deferred shading pipeline - pass 2 - light accumulation
		//alternate between 2 sets of textures and accumulate lighting computations
		lightShaderToggle = true;
		computeViewMatrixInv(); //compute inverse of view matrix
		computeEyeSpaceBound(renderer); //compute right and top boundaries of view frustum
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, lightBFbo[0]);
		gl2.glClear( GL2.GL_COLOR_BUFFER_BIT);

		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int lightCount = sMachine.getLightCount();
		for(int i=0; i<lightCount; ++i)
		{
			if(lightShaderToggle)
			{
				lightStart(gl2, renderer, i);
				drawQuad(gl2,renderer);
				lightStop(gl2);
			}
			else
			{
				lightStart(gl2, renderer, i);
				drawQuad(gl2,renderer);
				lightStop(gl2);
			}

			//switch shader for next light
			lightShaderToggle = !lightShaderToggle;
		}

		//drawTexture(lightATgtsTexture[1], gl2,renderer); 	//FOR DEBUG - see light buffer

		//3. deferred shading pipeline - pass 3 - coloring
		int texDiff,texSpec;
		if(lightShaderToggle)
		{
			texDiff=lightBTgtsTexture[0]; texSpec=lightBTgtsTexture[1];
		}
		else
		{
			texDiff=lightATgtsTexture[0]; texSpec=lightATgtsTexture[1];
		}
		colorStart(gl2, renderer,texDiff , texSpec);
		drawQuad(gl2,renderer);
		colorStop(gl2);
		
		drawTexture(colorTgtsTexture[0], gl2,renderer); 	//FOR DEBUG - see light buffer
		
	}

	private static void colorStop(GL2 gl2) {
		//unbind and disable textures
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE1);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE2);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE3);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glDisable(GL2.GL_TEXTURE_2D);

		gl2.glUseProgram(0);

		gl2.glPopAttrib();

		//switch back to window-system-provided framebuffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void colorStart(GL2 gl2, GvRenderer renderer, int texDiff, int texSpec) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		
		//orthogonal projection for drawing flat 2d image
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(0,sMachine.getScreenWidth(),0,sMachine.getScreenHeight(),0.1f,2);	
		//Model setup
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		
		gl2.glUseProgram(colorProgram[0]);
		
		int idWidthHeight = gl2.glGetUniformLocation(colorProgram[0],"widthHeight");

		//accumulated diffuse lights
		int idDiff = gl2.glGetUniformLocation(colorProgram[0],"tImage0");
		//accumulated specular lights
		int idSpec = gl2.glGetUniformLocation(colorProgram[0],"tImage1");
		//color buffer from g-buffer
		int idColor = gl2.glGetUniformLocation(colorProgram[0],"tImage2");

		//bind textures to uniform variables in shaders
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D,texDiff);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glUniform1i (idDiff, 0);

		gl2.glActiveTexture(GL2.GL_TEXTURE0 + 1);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D,texSpec);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glUniform1i (idSpec, 1);
		
		gl2.glActiveTexture(GL2.GL_TEXTURE0 + 2);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D,gBufferTgtsTexture[1]);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glUniform1i (idColor, 2);

		//bind screen dimensions to uniform variable widthHeight
		gl2.glUniform2f(idWidthHeight, (float)sMachine.getScreenWidth(), (float)sMachine.getScreenHeight());
		
		//Switch target frame buffer to fbo instance in this class
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, colorFbo[0]);
		gl2.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl2.glViewport(0,0,sMachine.getScreenWidth(), sMachine.getScreenHeight());

		//switch draw buffers to the render targets
		int buffers[] = new int[]{
				GL2.GL_COLOR_ATTACHMENT0
		};
		IntBuffer intBuffers = IntBuffer.wrap(buffers);
		gl2.glDrawBuffers(1, intBuffers);
	}

	/**
	 * Computes the inverse of the view matrix
	 */
	private static void computeViewMatrixInv() {
		double[][] viewMat2Array = new double[][]{
				{matView[0],matView[1],matView[2],matView[3]},
				{matView[4],matView[5],matView[6],matView[7]},
				{matView[8],matView[9],matView[10],matView[11]},
				{matView[12],matView[13],matView[14],matView[15]}
		};
		RealMatrix viewMatApache = new Array2DRowRealMatrix(viewMat2Array);
		RealMatrix vInverse = new LUDecomposition(viewMatApache).getSolver().getInverse();
		double[][] viewMatInv2Array = vInverse.getData();

		matViewInv[0]=(float)viewMatInv2Array[0][0];
		matViewInv[1]=(float)viewMatInv2Array[0][1];
		matViewInv[2]=(float)viewMatInv2Array[0][2];
		matViewInv[3]=(float)viewMatInv2Array[0][3];
		matViewInv[4]=(float)viewMatInv2Array[1][0];
		matViewInv[5]=(float)viewMatInv2Array[1][1];
		matViewInv[6]=(float)viewMatInv2Array[1][2];
		matViewInv[7]=(float)viewMatInv2Array[1][3];
		matViewInv[8]=(float)viewMatInv2Array[2][0];
		matViewInv[9]=(float)viewMatInv2Array[2][1];
		matViewInv[10]=(float)viewMatInv2Array[2][2];
		matViewInv[11]=(float)viewMatInv2Array[2][3];
		matViewInv[12]=(float)viewMatInv2Array[3][0];
		matViewInv[13]=(float)viewMatInv2Array[3][1];
		matViewInv[14]=(float)viewMatInv2Array[3][2];
		matViewInv[15]=(float)viewMatInv2Array[3][3];


	}

	private static void computeEyeSpaceBound(GvRenderer renderer)
	{
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		sMachine.getCamera(cameraInstance);

		eyeSpaceBound[1] = (float) (cameraInstance.lNear * Math.tan((cameraInstance.lFov/180.0)*Math.PI/2.0));
		eyeSpaceBound[0] = eyeSpaceBound[1]*cameraInstance.lAspect;
	}

	private static void lightStop(GL2 gl2) {
		//unbind and disable textures
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE1);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE2);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE3);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glDisable(GL2.GL_TEXTURE_2D);

		gl2.glUseProgram(0);

		gl2.glPopAttrib();

		//switch back to window-system-provided framebuffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void lightStart(GL2 gl2, GvRenderer renderer, int lightIndex) {

		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		sMachine.getCamera(cameraInstance);
		sMachine.getLight(lightIndex,lightInstance);

		//orthogonal projection for drawing flat 2d image
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		gl2.glOrtho(0,sMachine.getScreenWidth(),0,sMachine.getScreenHeight(),0.1f,2);	

		//Model setup
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		if(lightShaderToggle)
		{
			gl2.glUseProgram(lightAProgram[0]);

			//set values to be accessed by fragment shader
			int idPosition = gl2.glGetUniformLocation(lightAProgram[0],"tImage0"); //depth texture
			int idNormal = gl2.glGetUniformLocation(lightAProgram[0],"tImage1"); //normal texture
			int idPrevDiff = gl2.glGetUniformLocation(lightAProgram[0],"tImage2"); //previous accumulated light diffuse texture
			int idPrevSpec = gl2.glGetUniformLocation(lightAProgram[0],"tImage3"); //previous accumulated light specular texture

			int idLight = gl2.glGetUniformLocation(lightAProgram[0],"light"); //light position
			int idLightDiff = gl2.glGetUniformLocation(lightAProgram[0],"lightDiff"); //light diffuse color/intensity
			int idLightSpec = gl2.glGetUniformLocation(lightAProgram[0],"lightSpec"); //light specular color/intensity

			int idCameraPosition = gl2.glGetUniformLocation(lightAProgram[0],"cameraPosition");	//camera world position
			int idViewMatrixInv= gl2.glGetUniformLocation(lightAProgram[0],"viewMatrixInv"); //project matrix inverse

			int idClipPlanes= gl2.glGetUniformLocation(lightAProgram[0],"clipPlanes"); //znear and zfar
			int idWindowSize= gl2.glGetUniformLocation(lightAProgram[0],"windowSize"); //width and height
			int idRightAndTop= gl2.glGetUniformLocation(lightAProgram[0],"rightAndTop"); //eye space frustrum boundaries

			//set values
			gl2.glUniform3f(idLight,
					lightInstance.lPosition[0],
					lightInstance.lPosition[1],
					lightInstance.lPosition[2]);
			gl2.glUniform4f(idLightDiff,lightInstance.lDiffuse[0],
					lightInstance.lDiffuse[1],
					lightInstance.lDiffuse[2],
					lightInstance.lDiffuse[3]);
			gl2.glUniform4f(idLightSpec,lightInstance.lSpecular[0],
					lightInstance.lSpecular[1],
					lightInstance.lSpecular[2],
					lightInstance.lSpecular[3]);
			gl2.glUniform3f(idCameraPosition,
					cameraInstance.lPosition[0],
					cameraInstance.lPosition[1],
					cameraInstance.lPosition[2]);
			FloatBuffer viewInvWrap = FloatBuffer.wrap(matViewInv);
			gl2.glUniformMatrix4fv(idViewMatrixInv,1,false,viewInvWrap);
			gl2.glUniform2f(idClipPlanes, cameraInstance.lNear, cameraInstance.lFar);
			gl2.glUniform2f(idWindowSize, sMachine.getScreenWidth(), sMachine.getScreenHeight());
			gl2.glUniform2f(idRightAndTop,eyeSpaceBound[0],eyeSpaceBound[1]);

			gl2.glEnable(GL2.GL_TEXTURE_2D);

			gl2.glActiveTexture(GL2.GL_TEXTURE0);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,gBufferTgtsTexture[2]); //bind zbuffer texture
			gl2.glUniform1i (idPosition, 0);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 1);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,gBufferTgtsTexture[0]); //bind normal texture
			gl2.glUniform1i (idNormal, 1);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 2);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,lightAPrevDiff);
			gl2.glUniform1i (idPrevDiff, 2);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 3);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,lightAPrevSpec);
			gl2.glUniform1i(idPrevSpec, 3);

			//Switch target frame buffer to fbo instance in this class
			gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, lightAFbo[0]);
			gl2.glPushAttrib(GL2.GL_VIEWPORT_BIT);
			gl2.glViewport(0,0,sMachine.getScreenWidth(), sMachine.getScreenHeight());

			//switch draw buffers to the render targets
			int buffers[] = new int[]{
					GL2.GL_COLOR_ATTACHMENT0,
					GL2.GL_COLOR_ATTACHMENT1
			};
			IntBuffer intBuffers = IntBuffer.wrap(buffers);
			gl2.glDrawBuffers(2, intBuffers);
		}
		else
		{
			gl2.glUseProgram(lightBProgram[0]);

			//set values to be accessed by fragment shader
			int idPosition = gl2.glGetUniformLocation(lightBProgram[0],"tImage0"); //depth texture
			int idNormal = gl2.glGetUniformLocation(lightBProgram[0],"tImage1"); //normal texture
			int idPrevDiff = gl2.glGetUniformLocation(lightBProgram[0],"tImage2"); //previous accumulated light diffuse texture
			int idPrevSpec = gl2.glGetUniformLocation(lightBProgram[0],"tImage3"); //previous accumulated light specular texture

			int idLight = gl2.glGetUniformLocation(lightBProgram[0],"light"); //light position
			int idLightDiff = gl2.glGetUniformLocation(lightBProgram[0],"lightDiff"); //light diffuse color/intensity
			int idLightSpec = gl2.glGetUniformLocation(lightBProgram[0],"lightSpec"); //light specular color/intensity

			int idCameraPosition = gl2.glGetUniformLocation(lightBProgram[0],"cameraPosition");	//camera world position
			int idViewMatrixInv= gl2.glGetUniformLocation(lightBProgram[0],"viewMatrixInv"); //project matrix inverse

			int idClipPlanes= gl2.glGetUniformLocation(lightBProgram[0],"clipPlanes"); //znear and zfar
			int idWindowSize= gl2.glGetUniformLocation(lightBProgram[0],"windowSize"); //width and height
			int idRightAndTop= gl2.glGetUniformLocation(lightBProgram[0],"rightAndTop"); //eye space frustrum boundaries

			//set values
			gl2.glUniform3f(idLight,
					lightInstance.lPosition[0],
					lightInstance.lPosition[1],
					lightInstance.lPosition[2]);
			gl2.glUniform4f(idLightDiff,lightInstance.lDiffuse[0],
					lightInstance.lDiffuse[1],
					lightInstance.lDiffuse[2],
					lightInstance.lDiffuse[3]);
			gl2.glUniform4f(idLightSpec,lightInstance.lSpecular[0],
					lightInstance.lSpecular[1],
					lightInstance.lSpecular[2],
					lightInstance.lSpecular[3]);
			gl2.glUniform3f(idCameraPosition,
					cameraInstance.lPosition[0],
					cameraInstance.lPosition[1],
					cameraInstance.lPosition[2]);
			FloatBuffer viewInvWrap = FloatBuffer.wrap(matViewInv);
			gl2.glUniformMatrix4fv(idViewMatrixInv,1,false,viewInvWrap);
			gl2.glUniform2f(idClipPlanes, cameraInstance.lNear, cameraInstance.lFar);
			gl2.glUniform2f(idWindowSize, sMachine.getScreenWidth(), sMachine.getScreenHeight());
			gl2.glUniform2f(idRightAndTop,eyeSpaceBound[0],eyeSpaceBound[1]);

			gl2.glEnable(GL2.GL_TEXTURE_2D);

			gl2.glActiveTexture(GL2.GL_TEXTURE0);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,gBufferTgtsTexture[2]); //bind zbuffer texture
			gl2.glUniform1i (idPosition, 0);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 1);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,gBufferTgtsTexture[0]); //bind normal texture
			gl2.glUniform1i (idNormal, 1);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 2);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,lightBPrevDiff);
			gl2.glUniform1i (idPrevDiff, 2);

			gl2.glActiveTexture(GL2.GL_TEXTURE0 + 3);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D,lightBPrevSpec);
			gl2.glUniform1i(idPrevSpec, 3);

			//Switch target frame buffer to fbo instance in this class
			gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, lightBFbo[0]);
			gl2.glPushAttrib(GL2.GL_VIEWPORT_BIT);
			gl2.glViewport(0,0,sMachine.getScreenWidth(), sMachine.getScreenHeight());

			//switch draw buffers to the render targets
			int buffers[] = new int[]{
					GL2.GL_COLOR_ATTACHMENT0,
					GL2.GL_COLOR_ATTACHMENT1
			};
			IntBuffer intBuffers = IntBuffer.wrap(buffers);
			gl2.glDrawBuffers(2, intBuffers);
		}
	}

	private static void gBufferStop(GL2 gl2) {
		//DEBUG
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		gl2.glActiveTexture(GL2.GL_TEXTURE1);
		gl2.glBindTexture( GL2.GL_TEXTURE_2D, 0 );

		//disable g buffer fill shader
		gl2.glUseProgram(0);

		gl2.glPopAttrib();

		//switch back to window-system-provided framebuffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);

		//disable cull and depth tests
		gl2.glDisable(GL2.GL_CULL_FACE);
		gl2.glDisable(GL2.GL_DEPTH_TEST);
	}

	private static void gBufferStart(GL2 gl2,GvRenderer renderer) {
		//enable culling and depth test for writing to depth buffer
		gl2.glEnable(GL2.GL_CULL_FACE);
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		//use g buffer fill shading program
		//gl2.glUseProgram(programId);
		gl2.glUseProgram(gBufferProgram[0]);

		//render to fbo
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, gBufferFbo[0]);
		gl2.glPushAttrib(GL2.GL_VIEWPORT_BIT);
		gl2.glViewport(0,0,width, height);

		//clear color and depth
		gl2.glClearColor( 0, 0, 0, 0 );
		gl2.glClear( GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT );

		//set drawing buffers
		int buffers[] = new int[]{
				GL2.GL_COLOR_ATTACHMENT0,
				GL2.GL_COLOR_ATTACHMENT1
		};
		IntBuffer intBuffers = IntBuffer.wrap(buffers);
		gl2.glDrawBuffers(2, intBuffers);

		//set projection-model-view matrices
		sMachine.getCamera(cameraInstance);	//get camera info
		gl2.glMatrixMode(GL2.GL_PROJECTION);//set projection
		gl2.glLoadIdentity();
		glu.gluPerspective( cameraInstance.lFov,
				cameraInstance.lAspect,
				cameraInstance.lNear,
				cameraInstance.lFar	
				);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);	//set model-view
		gl2.glLoadIdentity();
		glu.gluLookAt(cameraInstance.lPosition[0],cameraInstance.lPosition[1],cameraInstance.lPosition[2],
				cameraInstance.lPosition[0]+cameraInstance.lView[0],
				cameraInstance.lPosition[1]+cameraInstance.lView[1],
				cameraInstance.lPosition[2]+cameraInstance.lView[2],
				cameraInstance.lUp[0], cameraInstance.lUp[1],cameraInstance.lUp[2]);

		//copy matrices - for pre-computing inverse to use in pass 2,3 
		gl2.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, matView, 1);		//copy model-view matrix for use in pass 2,3
		//gl2.glGetFloatv(GL2.GL_PROJECTION_MATRIX, matProjection, 1);//copy projection matrix for use in pass 2,3
		for(int i=0; i<15; ++i)
		{
			matView[i] = matView[i+1];
			//matProjection[i] = matProjection[i+1];
		}
		matView[15] = 1;
		//matProjection[15] = 0;
	}

	private static void drawObjVBO(GL2 gl2)
	{
		//bind vertex and normal buffer
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId[0]);
		//bind index buffer
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);


		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY); 
		gl2.glNormalPointer(GL2.GL_FLOAT, 0, vertices.length*4);

		gl2.glDrawElements(
				GL2.GL_TRIANGLES,      // mode
				indices.length,    // count
				GL2.GL_UNSIGNED_INT,   // type
				0           // element array buffer offset
				);

		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY); 
		gl2.glDisableClientState(GL2.GL_NORMAL_ARRAY); 
	}



	public static void reshape(GL2 gl2, int width, int height,
			GvRenderer lRenderer) {

		//set screen dimension changes into renderer state
		GvRendererStateMachine sMachine = lRenderer.getRendererStateMachine();
		if(sMachine.setState(GvRendererState.SCREEN_DIMENSIONS_CHANGE))
		{
			sMachine.screenSetDimensions(width, height);
			sMachine.setState(GvRendererState.IDLE);
		}

		//set viewport
		gl2.glViewport(0, 0, width, height);
		float aspectRatio = (float)width / (float)height;

		//set aspect ratio into camera instance
		if(sMachine.setState(GvRendererState.CAMERA_ASPECT_CHANGE))
		{
			lRenderer.getRendererStateMachine().cameraSetAspect(aspectRatio);
			lRenderer.getRendererStateMachine().setState(GvRendererState.IDLE);
		}

		//Re-initialize pipeline passes
		//pass 1
		gBufferDeleteAll(gl2);
		gBufferInit(gl2, lRenderer);
		//pass 2 - light A & B work in an altenating manner, i.e. the output of one is the input to the other
		lightADeleteAll(gl2);
		lightAInit(gl2, lRenderer);
		lightBDeleteAll(gl2);
		lightBInit(gl2, lRenderer);
		lightAPrevDiff=lightBTgtsTexture[0];
		lightAPrevSpec=lightBTgtsTexture[1];
		lightBPrevDiff=lightATgtsTexture[0];
		lightBPrevSpec=lightATgtsTexture[1];
		//pass 3 - color
		colorDeleteAll(gl2);
		colorInit(gl2, lRenderer);
	}

	private static void gBufferDeleteAll(GL2 gl2)
	{
		//delete shaders and program
		if((gBufferProgram!=null) && (gBufferShaderV!=null) && (gBufferShaderF!=null) )
		{
			gl2.glDetachShader(gBufferProgram[0], gBufferShaderV[0]);
			gl2.glDetachShader(gBufferProgram[0], gBufferShaderF[0]);
			gl2.glDeleteShader(gBufferShaderV[0]);
			gl2.glDeleteShader(gBufferShaderF[0]);
			gl2.glDeleteProgram(gBufferProgram[0]);
		}
		//delete frame buffer object
		if(gBufferFbo!=null)
		{
			IntBuffer fboIds = IntBuffer.wrap(gBufferFbo);
			gl2.glDeleteFramebuffers(1, fboIds);
		}

		//delete render buffers
		if(gBufferTgtsRender!=null)
		{	
			IntBuffer renderIds = IntBuffer.wrap(gBufferTgtsRender);
			gl2.glDeleteRenderbuffers(gBufferTargetCount, renderIds);
		}

		//delete textures
		if(gBufferTgtsTexture!=null)
		{
			IntBuffer textureIds = IntBuffer.wrap(gBufferTgtsTexture);
			gl2.glDeleteTextures(gBufferTargetCount, textureIds);
		}
	}

	private static void gBufferInit(GL2 gl2, GvRenderer renderer)
	{
		gBufferInitShaders(gl2);
		gBufferInitFrameBuffer(gl2);
		gBufferInitRenderBuffer(gl2,renderer);
		gBufferInitTextures(gl2,renderer);

		//check status of FBO after setup
		int status = gl2.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if( status != GL2.GL_FRAMEBUFFER_COMPLETE){
			gBufferDeleteAll(gl2);
			System.out.println("FBO: Error setting up frame buffer.\n");
			
			if(status == GL2.GL_FRAMEBUFFER_UNSUPPORTED)
			{
				System.out.println("     FBO: Unsupported.\n");
			}
		}

		//Bind back to default window-system-provided frame buffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void gBufferInitTextures(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate texture ids
		gl2.glGenTextures(gBufferTargetCount, gBufferTgtsTexture, 0);

		//setup texture target for normals and bind to fbo
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, gBufferTgtsTexture[0]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB16, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER,
				GL2.GL_COLOR_ATTACHMENT0,
				GL2.GL_TEXTURE_2D, 
				gBufferTgtsTexture[0], 
				0);

		//setup texture target for color and bind to fbo
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, gBufferTgtsTexture[1]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB16, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER,
				GL2.GL_COLOR_ATTACHMENT1,
				GL2.GL_TEXTURE_2D, 
				gBufferTgtsTexture[1], 
				0);

		//depth
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, gBufferTgtsTexture[2]);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH24_STENCIL8, width, height, 0, GL2.GL_DEPTH_STENCIL, GL2.GL_UNSIGNED_INT_24_8, null);
		gl2.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST );
		gl2.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST );
		gl2.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE );
		gl2.glTexParameteri( GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE );
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_DEPTH_ATTACHMENT, 
				GL2.GL_TEXTURE_2D, 
				gBufferTgtsTexture[2], 
				0);
	}

	private static void gBufferInitRenderBuffer(GL2 gl2, GvRenderer renderer) {
		//generate render buffer ids
		IntBuffer intBuffer = IntBuffer.wrap(gBufferTgtsRender);
		gl2.glGenRenderbuffers(gBufferTargetCount, intBuffer);

		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int w = sMachine.getScreenWidth();
		int h = sMachine.getScreenHeight();

		//bind render targets normals
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, gBufferTgtsRender[0]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB16, w,h);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, gBufferTgtsRender[0]);
		//bind render targets color
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, gBufferTgtsRender[1]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB16, w,h);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1, GL2.GL_RENDERBUFFER, gBufferTgtsRender[1]);
		//bind render targets depth
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, gBufferTgtsRender[2]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT24, w,h);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, gBufferTgtsRender[2]);
	}

	private static void gBufferInitFrameBuffer(GL2 gl2) {
		IntBuffer intBuffer = IntBuffer.wrap(gBufferFbo);

		//generate fbo
		gl2.glGenFramebuffers(1, intBuffer);

		//bind fbo
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, gBufferFbo[0]);
	}

	private static void gBufferInitShaders(GL2 gl2) {
		for(int i=0; i<gBufferShaderV.length; ++i)
		{
			gBufferShaderV[i] = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);

			//length of vertex shader program
			int[] vlen = new int[1];
			vlen[0] = TestRendererDeferredGBuffer.PROGRAM_V[i].length();

			//place vertex program in 1D array with 1 element
			String[] program = new String[]{TestRendererDeferredGBuffer.PROGRAM_V[i]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(gBufferShaderV[i], 1, program, vlen, 0);

			//compile vertex shader program
			gl2.glCompileShader(gBufferShaderV[i]);
		}

		for(int j=0; j<gBufferShaderF.length; ++j)
		{
			gBufferShaderF[j] = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

			/*
			 * Fragment Shader
			 */
			//length of fragment shader program
			int[] flen = new int[1];
			flen[0] = TestRendererDeferredGBuffer.PROGRAM_F[j].length();

			String[] program = new String[]{TestRendererDeferredGBuffer.PROGRAM_F[j]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(gBufferShaderF[j], 1, program, flen, 0);

			//compile vertex shader program
			gl2.glCompileShader(gBufferShaderF[j]);
		}

		/*
		 * Shader Program
		 */
		gBufferProgram[0] = gl2.glCreateProgram();
		gl2.glAttachShader(gBufferProgram[0],gBufferShaderV[0]);
		gl2.glAttachShader(gBufferProgram[0],gBufferShaderF[0]);
		gl2.glLinkProgram(gBufferProgram[0]);
		gl2.glValidateProgram(gBufferProgram[0]);

		System.out.println("GBuffer Shader: ");
		printLog(gl2,gBufferShaderV[0]);
		printLog(gl2,gBufferShaderF[0]);
		printLog(gl2,gBufferProgram[0]);
	}

	private static void lightAInit(GL2 gl2, GvRenderer renderer) {
		lightAInitShaders(gl2);
		lightAInitFrameBuffer(gl2);
		lightAInitRenderBuffer(gl2,renderer);
		lightAInitTextures(gl2,renderer);

		//check status of FBO after setup
		int status = gl2.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if( status != GL2.GL_FRAMEBUFFER_COMPLETE){
			gBufferDeleteAll(gl2);
			System.out.println("Error setting up frame buffer.\n");
		}

		//Bind back to default window-system-provided frame buffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void lightAInitTextures(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate texture ids
		gl2.glGenTextures(lightTargetCount, lightATgtsTexture, 0);

		//setup texture target for position and bind to fbo

		//texture for diffuse component - Set A
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, lightATgtsTexture[0]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT0, 
				GL2.GL_TEXTURE_2D, 
				lightATgtsTexture[0],
				0);

		//texture for specular component - Set A
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, lightATgtsTexture[1]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT1, 
				GL2.GL_TEXTURE_2D, 
				lightATgtsTexture[1],
				0);
	}

	private static void lightAInitRenderBuffer(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate render buffer ids
		IntBuffer intBuffer = IntBuffer.wrap(lightATgtsRender);
		gl2.glGenRenderbuffers(lightTargetCount, intBuffer);

		//bind render targets to fbo

		//render target 1 - diffuse component A
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, lightATgtsRender[0]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB32F, width, height);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, lightATgtsRender[0]);
		//render target 2 - specular component A
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, lightATgtsRender[1]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB32F, width, height);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1, GL2.GL_RENDERBUFFER, lightATgtsRender[1]);
	}

	private static void lightAInitFrameBuffer(GL2 gl2) {
		IntBuffer intBuffer = IntBuffer.wrap(lightAFbo);

		//generate fbo
		gl2.glGenFramebuffers(1, intBuffer);

		//bind fbo
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, lightAFbo[0]);
	}

	private static void lightAInitShaders(GL2 gl2) {
		for(int i=0; i<lightAShaderV.length; ++i)
		{
			lightAShaderV[i] = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);

			//length of vertex shader program
			int[] vlen = new int[1];
			vlen[0] = TestRendererDeferredLight.PROGRAM_V[i].length();

			//place vertex program in 1D array with 1 element
			String[] program = new String[]{TestRendererDeferredLight.PROGRAM_V[i]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(lightAShaderV[i], 1, program, vlen, 0);

			//compile vertex shader program
			gl2.glCompileShader(lightAShaderV[i]);
		}

		for(int j=0; j<lightAShaderF.length; ++j)
		{
			lightAShaderF[j] = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

			/*
			 * Fragment Shader
			 */
			//length of fragment shader program
			int[] flen = new int[1];
			flen[0] = TestRendererDeferredLight.PROGRAM_F[j].length();

			String[] program = new String[]{TestRendererDeferredLight.PROGRAM_F[j]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(lightAShaderF[j], 1, program, flen, 0);

			//compile vertex shader program
			gl2.glCompileShader(lightAShaderF[j]);
		}

		/*
		 * Shader Program
		 */
		lightAProgram[0] = gl2.glCreateProgram();
		gl2.glAttachShader(lightAProgram[0],lightAShaderV[0]);
		gl2.glAttachShader(lightAProgram[0],lightAShaderF[0]);
		gl2.glLinkProgram(lightAProgram[0]);
		gl2.glValidateProgram(lightAProgram[0]);

		System.out.println("Light Shader A: ");
		printLog(gl2,lightAShaderV[0]);
		printLog(gl2,lightAShaderF[0]);
		printLog(gl2,lightAProgram[0]);
	}

	private static void lightADeleteAll(GL2 gl2) {
		//delete shaders and program
		if((lightAProgram!=null) && (lightAShaderV!=null) && (lightAShaderF!=null) )
		{
			gl2.glDetachShader(lightAProgram[0], lightAShaderV[0]);
			gl2.glDetachShader(lightAProgram[0], lightAShaderF[0]);
			gl2.glDeleteShader(lightAShaderV[0]);
			gl2.glDeleteShader(lightAShaderF[0]);
			gl2.glDeleteProgram(lightAProgram[0]);
		}
		//delete frame buffer object
		if(lightAFbo!=null)
		{
			IntBuffer fboIds = IntBuffer.wrap(lightAFbo);
			gl2.glDeleteFramebuffers(1, fboIds);
		}

		//delete render buffers
		if(lightATgtsRender!=null)
		{	
			IntBuffer renderIds = IntBuffer.wrap(lightATgtsRender);
			gl2.glDeleteRenderbuffers(lightTargetCount, renderIds);
		}

		//delete textures
		if(lightATgtsTexture!=null)
		{
			IntBuffer textureIds = IntBuffer.wrap(lightATgtsTexture);
			gl2.glDeleteTextures(lightTargetCount, textureIds);
		}
	}

	private static void lightBInit(GL2 gl2, GvRenderer renderer) {
		lightBInitShaders(gl2);
		lightBInitFrameBuffer(gl2);
		lightBInitRenderBuffer(gl2,renderer);
		lightBInitTextures(gl2,renderer);

		//check status of FBO after setup
		int status = gl2.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if( status != GL2.GL_FRAMEBUFFER_COMPLETE){
			gBufferDeleteAll(gl2);
			System.out.println("Error setting up frame buffer.\n");
		}

		//Bind back to default window-system-provided frame buffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void lightBInitTextures(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate texture ids
		gl2.glGenTextures(lightTargetCount, lightBTgtsTexture, 0);

		//setup texture target for position and bind to fbo

		//texture for diffuse component - Set A
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, lightBTgtsTexture[0]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT0, 
				GL2.GL_TEXTURE_2D, 
				lightBTgtsTexture[0],
				0);

		//texture for specular component - Set A
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, lightBTgtsTexture[1]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT1, 
				GL2.GL_TEXTURE_2D, 
				lightBTgtsTexture[1],
				0);
	}

	private static void lightBInitRenderBuffer(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate render buffer ids
		IntBuffer intBuffer = IntBuffer.wrap(lightBTgtsRender);
		gl2.glGenRenderbuffers(lightTargetCount, intBuffer);

		//bind render targets to fbo

		//render target 1 - diffuse component A
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, lightBTgtsRender[0]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB32F, width, height);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, lightBTgtsRender[0]);
		//render target 2 - specular component A
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, lightBTgtsRender[1]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB32F, width, height);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT1, GL2.GL_RENDERBUFFER, lightBTgtsRender[1]);
	}

	private static void lightBInitFrameBuffer(GL2 gl2) {
		IntBuffer intBuffer = IntBuffer.wrap(lightBFbo);

		//generate fbo
		gl2.glGenFramebuffers(1, intBuffer);

		//bind fbo
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, lightBFbo[0]);
	}

	private static void lightBInitShaders(GL2 gl2) {
		for(int i=0; i<lightBShaderV.length; ++i)
		{
			lightBShaderV[i] = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);

			//length of vertex shader program
			int[] vlen = new int[1];
			vlen[0] = TestRendererDeferredLight.PROGRAM_V[i].length();

			//place vertex program in 1D array with 1 element
			String[] program = new String[]{TestRendererDeferredLight.PROGRAM_V[i]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(lightBShaderV[i], 1, program, vlen, 0);

			//compile vertex shader program
			gl2.glCompileShader(lightBShaderV[i]);
		}

		for(int j=0; j<lightBShaderF.length; ++j)
		{
			lightBShaderF[j] = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

			/*
			 * Fragment Shader
			 */
			//length of fragment shader program
			int[] flen = new int[1];
			flen[0] = TestRendererDeferredLight.PROGRAM_F[j].length();

			String[] program = new String[]{TestRendererDeferredLight.PROGRAM_F[j]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(lightBShaderF[j], 1, program, flen, 0);

			//compile vertex shader program
			gl2.glCompileShader(lightBShaderF[j]);
		}

		/*
		 * Shader Program
		 */
		lightBProgram[0] = gl2.glCreateProgram();
		gl2.glAttachShader(lightBProgram[0],lightBShaderV[0]);
		gl2.glAttachShader(lightBProgram[0],lightBShaderF[0]);
		gl2.glLinkProgram(lightBProgram[0]);
		gl2.glValidateProgram(lightBProgram[0]);
		
		System.out.println("Light Shader B: ");
		printLog(gl2,lightBShaderV[0]);
		printLog(gl2,lightBShaderF[0]);
		printLog(gl2,lightBProgram[0]);
	}

	private static void lightBDeleteAll(GL2 gl2) {
		//delete shaders and program
		if((lightBProgram!=null) && (lightBShaderV!=null) && (lightBShaderF!=null) )
		{
			gl2.glDetachShader(lightBProgram[0], lightBShaderV[0]);
			gl2.glDetachShader(lightBProgram[0], lightBShaderF[0]);
			gl2.glDeleteShader(lightBShaderV[0]);
			gl2.glDeleteShader(lightBShaderF[0]);
			gl2.glDeleteProgram(lightBProgram[0]);
		}
		//delete frame buffer object
		if(lightBFbo!=null)
		{
			IntBuffer fboIds = IntBuffer.wrap(lightBFbo);
			gl2.glDeleteFramebuffers(1, fboIds);
		}

		//delete render buffers
		if(lightBTgtsRender!=null)
		{	
			IntBuffer renderIds = IntBuffer.wrap(lightBTgtsRender);
			gl2.glDeleteRenderbuffers(lightTargetCount, renderIds);
		}

		//delete textures
		if(lightBTgtsTexture!=null)
		{
			IntBuffer textureIds = IntBuffer.wrap(lightBTgtsTexture);
			gl2.glDeleteTextures(lightTargetCount, textureIds);
		}
	}

	private static void colorInit(GL2 gl2, GvRenderer renderer) {
		colorInitShaders(gl2);
		colorInitFrameBuffer(gl2);
		colorInitRenderBuffer(gl2,renderer);
		colorInitTextures(gl2,renderer);

		//check status of FBO after setup
		int status = gl2.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		if( status != GL2.GL_FRAMEBUFFER_COMPLETE){
			gBufferDeleteAll(gl2);
			System.out.println("Error setting up frame buffer.\n");
		}

		//Bind back to default window-system-provided frame buffer
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
	}

	private static void colorInitTextures(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate texture ids
		gl2.glGenTextures(colorTargetCount, colorTgtsTexture, 0);

		//setup texture target for position and bind to fbo

		//texture for diffuse component - Set A
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, colorTgtsTexture[0]);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
		gl2.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB32F, width, height, 0, GL2.GL_RGB, GL2.GL_FLOAT, null);
		gl2.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, 
				GL2.GL_COLOR_ATTACHMENT0, 
				GL2.GL_TEXTURE_2D, 
				colorTgtsTexture[0],
				0);
	}

	private static void colorInitRenderBuffer(GL2 gl2, GvRenderer renderer) {
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//generate render buffer ids
		IntBuffer intBuffer = IntBuffer.wrap(colorTgtsRender);
		gl2.glGenRenderbuffers(colorTargetCount, intBuffer);

		//bind render targets to fbo

		//render target 1 - diffuse component A
		gl2.glBindRenderbuffer(GL2.GL_RENDERBUFFER, colorTgtsRender[0]);
		gl2.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_RGB32F, width, height);
		gl2.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_RENDERBUFFER, colorTgtsRender[0]);
	}

	private static void colorInitFrameBuffer(GL2 gl2) {
		IntBuffer intBuffer = IntBuffer.wrap(colorFbo);

		//generate fbo
		gl2.glGenFramebuffers(1, intBuffer);

		//bind fbo
		gl2.glBindFramebuffer(GL2.GL_FRAMEBUFFER, colorFbo[0]);
	}

	private static void colorInitShaders(GL2 gl2) {
		for(int i=0; i<colorShaderV.length; ++i)
		{
			colorShaderV[i] = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);

			//length of vertex shader program
			int[] vlen = new int[1];
			vlen[0] = TestRendererDeferredColor.PROGRAM_V[i].length();

			//place vertex program in 1D array with 1 element
			String[] program = new String[]{TestRendererDeferredColor.PROGRAM_V[i]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(colorShaderV[i], 1, program, vlen, 0);

			//compile vertex shader program
			gl2.glCompileShader(colorShaderV[i]);
		}

		for(int j=0; j<colorShaderF.length; ++j)
		{
			colorShaderF[j] = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

			/*
			 * Fragment Shader
			 */
			//length of fragment shader program
			int[] flen = new int[1];
			flen[0] = TestRendererDeferredColor.PROGRAM_F[j].length();

			String[] program = new String[]{TestRendererDeferredColor.PROGRAM_F[j]};

			//link vertex shader id and vertex program
			gl2.glShaderSource(colorShaderF[j], 1, program, flen, 0);

			//compile vertex shader program
			gl2.glCompileShader(colorShaderF[j]);
		}

		/*
		 * Shader Program
		 */
		colorProgram[0] = gl2.glCreateProgram();
		gl2.glAttachShader(colorProgram[0],colorShaderV[0]);
		gl2.glAttachShader(colorProgram[0],colorShaderF[0]);
		gl2.glLinkProgram(colorProgram[0]);
		gl2.glValidateProgram(colorProgram[0]);

		System.out.println("Color Shader: ");
		printLog(gl2,colorShaderV[0]);
		printLog(gl2,colorShaderF[0]);
		printLog(gl2,colorProgram[0]);
	}

	private static void colorDeleteAll(GL2 gl2) {
		//delete shaders and program
		if((colorProgram!=null) && (colorShaderV!=null) && (colorShaderF!=null) )
		{
			gl2.glDetachShader(colorProgram[0], colorShaderV[0]);
			gl2.glDetachShader(colorProgram[0], colorShaderF[0]);
			gl2.glDeleteShader(colorShaderV[0]);
			gl2.glDeleteShader(colorShaderF[0]);
			gl2.glDeleteProgram(colorProgram[0]);
		}
		//delete frame buffer object
		if(colorFbo!=null)
		{
			IntBuffer fboIds = IntBuffer.wrap(colorFbo);
			gl2.glDeleteFramebuffers(1, fboIds);
		}

		//delete render buffers
		if(colorTgtsRender!=null)
		{	
			IntBuffer renderIds = IntBuffer.wrap(colorTgtsRender);
			gl2.glDeleteRenderbuffers(colorTargetCount, renderIds);
		}

		//delete textures
		if(colorTgtsTexture!=null)
		{
			IntBuffer textureIds = IntBuffer.wrap(colorTgtsTexture);
			gl2.glDeleteTextures(colorTargetCount, textureIds);
		}
	}

	/**
	 * Draws a quad the size of the viewport
	 * @param gl2
	 * @param renderer
	 */
	private static void drawQuad(GL2 gl2, GvRenderer renderer)
	{
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//draw texture
		gl2.glColor3f(1,1,1);
		gl2.glBegin(GL2.GL_QUADS);
		//gl2.glTexCoord2f(0, 1);
		gl2.glVertex3f(0.0f, height, -1.0f);
		//gl2.glTexCoord2f(0, 0);
		gl2.glVertex3f(0.0f, 0.0f  , -1.0f);
		//gl2.glTexCoord2f(1, 0);
		gl2.glVertex3f(width , 0.0f  , -1.0f);
		//gl2.glTexCoord2f(1, 1);
		gl2.glVertex3f(width , height, -1.0f);
		gl2.glEnd();
	}

	/**
	 * Draws a quad the size of the viewport with the specified texture
	 * @param textureId
	 * @param gl2
	 * @param renderer
	 */
	public static void drawTexture(int textureId ,GL2 gl2, GvRenderer renderer)
	{
		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		int width = sMachine.getScreenWidth();
		int height = sMachine.getScreenHeight();

		//orthogonal projection for drawing flat 2d image
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();
		gl2.glOrtho(0,width,0,height,0.1f,2);	

		//disable lighting
		gl2.glDisable(GL2.GL_LIGHTING);

		//Model setup
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadIdentity();

		//bind texture
		gl2.glActiveTexture(GL2.GL_TEXTURE0);
		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, textureId);

		//move image slightly to the back
		gl2.glTranslatef(0,0,-1.0f);

		//draw texture
		gl2.glColor3f(1,1,1);
		gl2.glBegin(GL2.GL_QUADS);
		gl2.glTexCoord2f(0, 1);
		gl2.glVertex3f(0.0f, height, 0.0f);
		gl2.glTexCoord2f(0, 0);
		gl2.glVertex3f(0.0f, 0.0f  , 0.0f);
		gl2.glTexCoord2f(1, 0);
		gl2.glVertex3f(width , 0.0f  , 0.0f);
		gl2.glTexCoord2f(1, 1);
		gl2.glVertex3f(width , height, 0.0f);
		gl2.glEnd();

		//enable back lighting
		gl2.glEnable(GL2.GL_LIGHTING);

		//unbind texture
		gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);

		//Reset the matrices	
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPopMatrix();
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPopMatrix();
	}

	static void printLog(GL2 gl2, int obj)
	{
		int maxLen[] = new int[1];
		IntBuffer maxLength = IntBuffer.wrap(maxLen);

		if(gl2.glIsShader(obj))
			gl2.glGetShaderiv(obj,GL2.GL_INFO_LOG_LENGTH,maxLength);
		else
			gl2.glGetProgramiv(obj,GL2.GL_INFO_LOG_LENGTH,maxLength);

		int len = maxLen[0];
		byte infoLog[] = new byte[len];
		ByteBuffer infoLogBuffer = ByteBuffer.wrap(infoLog);

		int infoLen[] = new int[1];
		IntBuffer infoLength = IntBuffer.wrap(infoLen);

		if (gl2.glIsShader(obj))
			gl2.glGetShaderInfoLog(obj, maxLen[0], infoLength, infoLogBuffer);
		else
			gl2.glGetProgramInfoLog(obj, maxLen[0], infoLength, infoLogBuffer);

		if (infoLen[0] > 0)
		{
			System.out.println(new String(infoLog));
		}
	}
}

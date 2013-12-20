package de.grovie.test.engine.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.GvRendererStateMachine;
import de.grovie.engine.renderer.GL2.GvRendererGL2;
import de.grovie.engine.renderer.GvRendererStateMachine.RendererState;
import de.grovie.engine.renderer.device.GvCamera;
import de.grovie.engine.renderer.windowsystem.AWT.GvWindowSystemAWT;

public class TestRendererDeferred {

	//camera
	static GvCamera cameraInstance = new GvCamera();

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
	

	public static void main(String[] args) {
		//create windowing system - Java AWT
		GvWindowSystemAWT windowSystem = new GvWindowSystemAWT();

		//create renderer to use - OpenGL 3x
		GvRendererGL2 gvRenderer = new GvRendererGL2(
				windowSystem,
				"Test VBO",
				640,
				480);

		//test draw obj file
		initObj();

		gvRenderer.start();

	}

	private static void initObj() {
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\teapot\\teapot2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\dragon\\dragon2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\sponza.obj";
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
		gBufferTgtsRender  = new int[3];
		gBufferTgtsTexture = new int[3];
		gBufferFbo = new int[1];

		//		gBufferDeleteAll(gl2);
		//		gBufferInit(gl2, renderer);
		//
		//		lightADeleteAll(gl2);
		//		lightAInit(gl2);
		//
		//		lightBDeleteAll(gl2);
		//		lightBInit(gl2);
		//
		//		colorDeleteAll(gl2);
		//		colorInit(gl2);
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

		//deferred lighting pipeline - pass 1 - gbuffer
		gBufferStart(gl2,renderer);
			gl2.glMatrixMode(GL2.GL_MODELVIEW);
			gl2.glLoadIdentity();
			renderer.getRendererStateMachine().getCamera(cameraInstance);
			glu.gluLookAt(cameraInstance.lPosition[0],cameraInstance.lPosition[1],cameraInstance.lPosition[2],  /* eye is at (0,0,5) */
					cameraInstance.lPosition[0]+cameraInstance.lView[0],
					cameraInstance.lPosition[1]+cameraInstance.lView[1],      /* center is at (0,0,0) */
					cameraInstance.lPosition[2]+cameraInstance.lView[2],
					cameraInstance.lUp[0], cameraInstance.lUp[1],cameraInstance.lUp[2]);      /* up is in positive Y direction */
			//gl2.glColor4f(1.0f,0,0,1.0f);
			drawObjVBO(gl2);
		gBufferStop(gl2);

		drawTexture(gBufferTgtsTexture[0], gl2,renderer);	//FOR DEBUG - see normal buffer
		//drawTexture(gBufferTgtsTexture[1], gl2,renderer);	//FOR DEBUG - see normal buffer
		//drawTexture(gBufferTgtsTexture[2], gl2,renderer); //FOR DEBUG - see depth buffer
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

		GvRendererStateMachine sMachine = renderer.getRendererStateMachine();
		sMachine.getCamera(cameraInstance);

		/* Setup the view of the cube. */
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective( cameraInstance.lFov,
				cameraInstance.lAspect,
				cameraInstance.lNear,
				cameraInstance.lFar	
				);

		//use g buffer fill shading program
		//gl2.glUseProgram(programId);
		gl2.glUseProgram(gBufferProgram[0]);

		//render to fbo
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

		GvRendererStateMachine sMachine = lRenderer.getRendererStateMachine();
		if(sMachine.setState(RendererState.SCREEN_DIMENSIONS_CHANGE))
		{
			sMachine.screenSetDimensions(width, height);
			sMachine.setState(RendererState.IDLE);
		}

		gl2.glViewport(0, 0, width, height);
		float aspectRatio = (float)width / (float)height;

		//set aspect ratio into camera instance
		if(sMachine.setState(RendererState.CAMERA_ASPECT_CHANGE))
		{
			lRenderer.getRendererStateMachine().cameraSetAspect(aspectRatio);
			lRenderer.getRendererStateMachine().setState(RendererState.IDLE);
		}

		gBufferDeleteAll(gl2);
		gBufferInit(gl2, lRenderer);

		lightADeleteAll(gl2);
		lightAInit(gl2);

		lightBDeleteAll(gl2);
		lightBInit(gl2);

		colorDeleteAll(gl2);
		colorInit(gl2);
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
			gl2.glDeleteRenderbuffers(2, renderIds);
		}
	}

	private static void gBufferInit(GL2 gl2, GvRenderer renderer)
	{
		gBufferInitShaders(gl2);
		gBufferInitFrameBuffer(gl2);
		gBufferInitRenderBuffer(gl2,renderer);
		gBufferInitTextures(gl2,renderer);

		//check status of FBO after setup
		//int status = gl2.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
		//if( status != GL2.GL_FRAMEBUFFER_COMPLETE){
		//	gBufferDeleteAll(gl2);
		//	System.out.println("Error setting up frame buffer.\n");
		//}

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
	}

	private static void lightAInit(GL2 gl2) {
		// TODO Auto-generated method stub

	}

	private static void lightADeleteAll(GL2 gl2) {
		// TODO Auto-generated method stub

	}

	private static void lightBInit(GL2 gl2) {
		// TODO Auto-generated method stub

	}

	private static void lightBDeleteAll(GL2 gl2) {
		// TODO Auto-generated method stub

	}

	private static void colorInit(GL2 gl2) {
		// TODO Auto-generated method stub

	}

	private static void colorDeleteAll(GL2 gl2) {
		// TODO Auto-generated method stub

	}

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
}

package de.grovie.test.engine.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.engine.renderer.GL3.GvRendererGL3;
import de.grovie.engine.renderer.windowsystem.AWT.GvWindowSystemAWT;

public class TestRenderer {

	/**
	 * Utility method instances
	 */
	static GLU glu;
	static GLUT glut;

	/**
	 * Light parameters
	 */
	static float light_diffuse[] = {1.0f, 0.0f, 0.0f, 1.0f};  /* Red diffuse light. */
	static float light_position[] = {1.0f, 1.0f, 1.0f, 0.0f};  /* Infinite light location. */

	/**
	 * Variables for standard drawing
	 */
	static float n[][] = {  /* Normals for the 6 faces of a cube. */
		{-1.0f, 0.0f, 0.0f},
		{0.0f, 1.0f, 0.0f},
		{1.0f, 0.0f, 0.0f},
		{0.0f, -1.0f, 0.0f},
		{0.0f, 0.0f, 1.0f},
		{0.0f, 0.0f, -1.0f} };
	static int faces[][] = {  /* Vertex indices for the 6 faces of a cube. */
		{0, 1, 2, 3}, {3, 2, 6, 7}, {7, 6, 5, 4},
		{4, 5, 1, 0}, {5, 6, 2, 1}, {7, 4, 0, 3} };
	public static float[][] v = new float[8][3];


	/**
	 * Variables for VBO drawing
	 */

	public static int[] vboId; //vbo Id
	public static int[] iboId; //index array object id
	public static float vertices[]; //vertices
	public static float normals[]; 	//normals
	public static int indices[] = { //vertex indices
		0,1,3,
		1,2,3,
		3,2,7,
		7,2,6,
		7,6,5,
		7,5,4,
		4,5,1,
		0,5,1,
		5,6,2,
		6,2,1,
		7,4,0,
		4,0,3
	};
	public static String[] shaderV = {
		"void main()"+
				"{"+
				"	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;"+
				"}"
	};

	public static String[] shaderF = { 
		"void main()"+
				"{"+
				"	gl_FragColor = vec4(0.4,0.4,0.8,1.0);"+
				"}"
	};
	public static int shaderVId;
	public static int shaderFId;
	public static int shaderProgramId;


	public static void main(String[] args)
	{
		//create windowing system - Java AWT
		GvWindowSystemAWT windowSystem = new GvWindowSystemAWT();

		//create renderer to use - OpenGL 3x
		GvRendererGL3 gvRenderer = new GvRendererGL3(
				windowSystem,
				"Test VBO",
				640,
				480);

		gvRenderer.start();

		initVertexData();
	}

	public static void setup( GL2 gl2, int width, int height ) {
		initGL(gl2);

		initShaders(gl2);
		initVBOs(gl2);
	}

	private static void initVertexData()
	{
		/* Setup cube vertex data. */
		v[0][0] = v[1][0] = v[2][0] = v[3][0] = -1;
		v[4][0] = v[5][0] = v[6][0] = v[7][0] = 1;
		v[0][1] = v[1][1] = v[4][1] = v[5][1] = -1;
		v[2][1] = v[3][1] = v[6][1] = v[7][1] = 1;
		v[0][2] = v[3][2] = v[4][2] = v[7][2] = 1;
		v[1][2] = v[2][2] = v[5][2] = v[6][2] = -1;

		vertices = new float[v.length * v[0].length];
		for(int i=0; i< v.length ; ++i)
		{
			for(int j=0; j< v[0].length; ++j)
			{
				vertices[(v[0].length*i)+j] = v[i][j];
			}
		}

		normals = new float[n.length * n[0].length];
		for(int i=0; i< n.length ; ++i)
		{
			for(int j=0; j< n[0].length; ++j)
			{
				normals[(n[0].length*i)+j] = n[i][j];
			}
		}

		System.out.println("vertices for VBO");
		printArray(vertices);
		System.out.println("normals for VBO");
		printArray(normals);
	}


	private static void initShaders(GL2 gl2) {
		//create shaders
		shaderVId = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
		shaderFId = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);

		//set shader sources
		gl2.glShaderSource(shaderVId, 1, shaderV, (int[]) null, 0);
		gl2.glShaderSource(shaderFId, 1, shaderF, (int[]) null, 0);

		//compile shaders
		gl2.glCompileShader(shaderVId);
		gl2.glCompileShader(shaderFId);

		//create program
		shaderProgramId = gl2.glCreateProgram();
		gl2.glAttachShader(shaderProgramId, shaderVId);
		gl2.glAttachShader(shaderProgramId, shaderFId);

		gl2.glLinkProgram(shaderProgramId);
		gl2.glValidateProgram(shaderProgramId);
		gl2.glUseProgram(shaderProgramId); 
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

	private static void initGL(GL2 gl2)
	{
		/* Enable a single OpenGL light. */
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light_diffuse,0);
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, light_position,0);
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glEnable(GL2.GL_LIGHTING);

		/* Use depth buffering for hidden surface elimination. */
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		glu = new GLU();
		glut = new GLUT();

		/* Setup the view of the cube. */
		gl2.glMatrixMode(GL2.GL_PROJECTION);

		glu.gluPerspective( /* field of view in degree */ 40.0,
				/* aspect ratio */ 1.0,
				/* Z near */ 1.0, /* Z far */ 10.0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		glu.gluLookAt(0.0, 0.0, 5.0,  /* eye is at (0,0,5) */
				0.0, 0.0, 0.0,      /* center is at (0,0,0) */
				0.0, 1.0, 0.);      /* up is in positive Y direction */

		/* Adjust cube position to be asthetic angle. */
		gl2.glTranslatef(0.0f, 0.0f, -1.0f);
		gl2.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
		gl2.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
	}

	public static void render( GL2 gl2, int width, int height ) {
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
		//drawBoxStandard(gl2);
		drawBoxVBO(gl2);
	}

	private static void	drawBoxStandard(GL2 gl2)
	{
		int i;

		for (i = 0; i < 6; i++) {
			gl2.glBegin(GL2.GL_QUADS);

			FloatBuffer norm = FloatBuffer.wrap(n[i]);
			gl2.glNormal3fv(norm);// (&n[i][0]);

			FloatBuffer v0 = FloatBuffer.wrap(v[faces[i][0]]);
			FloatBuffer v1 = FloatBuffer.wrap(v[faces[i][1]]);
			FloatBuffer v2 = FloatBuffer.wrap(v[faces[i][2]]);
			FloatBuffer v3 = FloatBuffer.wrap(v[faces[i][3]]);

			gl2.glVertex3fv(v0);
			gl2.glVertex3fv(v1);
			gl2.glVertex3fv(v2);
			gl2.glVertex3fv(v3);
			gl2.glEnd();
		}
	}

	private static void drawBoxVBO(GL2 gl2)
	{
		//bind vertex and normal buffer
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId[0]);
		//bind index buffer
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboId[0]);


		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
		gl2.glNormalPointer(GL2.GL_FLOAT, 0, vertices.length*4);

		gl2.glDrawElements(
				GL2.GL_TRIANGLES,      // mode
				indices.length,    // count
				GL2.GL_UNSIGNED_INT,   // type
				0           // element array buffer offset
				);

		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY); 
	}

	private static void printArray(float[] arr)
	{
		for(int i=0; i<arr.length; ++i)
		{
			System.out.println(arr[i]);
		}
	}
}

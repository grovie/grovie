package de.grovie.test.engine.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.GL3.GvRendererGL3;
import de.grovie.engine.renderer.windowsystem.AWT.GvWindowSystemAWT;

public class TestRendererDeferred {

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
	
	public static void main(String[] args) {
		//create windowing system - Java AWT
		GvWindowSystemAWT windowSystem = new GvWindowSystemAWT();

		//create renderer to use - OpenGL 3x
		GvRendererGL3 gvRenderer = new GvRendererGL3(
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
		//String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);

		indices = geom.getIndices();
		vertices = geom.getVertices();
		normals = geom.getNormals();
		System.out.println("Polygon count: " + indices.length/3);
	}

	public static void init(GL2 gl2) {
		initGL(gl2);
		initShaders(gl2);
		initVBOs(gl2);
	}
	
	private static void initShaders(GL2 gl2) {
		// TODO Auto-generated method stub
		
	}

	private static void initGL(GL2 gl2) {
		
		gl2.glDisable(GL2.GL_LIGHTING);

		/* Use depth buffering for hidden surface elimination. */
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		glu = new GLU();
		glut = new GLUT();

		/* Setup the view of the cube. */
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective( /* field of view in degree */ 60.0,
				/* aspect ratio */ 1.0,
				/* Z near */ 0.1, /* Z far */ 100.0);

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
			GvRenderer lRenderer) {
		// TODO Auto-generated method stub
		
	}
	
	

	public static void setup(GL2 gl2, int width, int height) {
		// TODO Auto-generated method stub
		
	}
}

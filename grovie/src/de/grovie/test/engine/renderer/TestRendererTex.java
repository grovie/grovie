package de.grovie.test.engine.renderer;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.data.object.GvGeometryTex;
import de.grovie.renderer.GvCamera;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GL2.GvRendererGL2;
import de.grovie.renderer.windowsystem.AWT.GvWindowSystemAWTGL;
import static de.grovie.renderer.GvRendererStateMachine.GvRendererState;

public class TestRendererTex {

	/**
	 * Frame time
	 */
	static double frameTime;
	
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
	 * Initial Camera settings
	 */
	static float cameraPosition[] = {0,0,5.0f};
	static float cameraUp[] = {0,1,0};
	static float cameraCenter[] = {0,0,-1.0f};
	static GvCamera cameraInstance = new GvCamera();

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
	 * VAO
	 */
	public static int[] vaoId; //vao ID
	
	
	/**
	 * Variables for VBO drawing
	 */

	public static int[] vboId; //vbo Id
	public static int[] iboId; //index array object id
	public static float vertices[]; //vertices
	public static float normals[]; 	//normals
	public static int indices[];	//vertex indices
	public static float uv[];		//uv coords
	public static String[] shaderV = {
		"uniform vec3 cameraPos;"+ //camera position world space
				"uniform vec4 globalAmb;"+ //global ambient
				"uniform vec3 lightDir;"+ //directional light position - also the direction vector (normlized) from vertices to light position
				"uniform vec4 lightAmb;"+ //directional light ambient
				"uniform vec4 lightDif;"+ //directional light diffuse
				"uniform vec4 lightSpe;"+ //directional light specular
				"uniform vec4 materialAmb;"+ //material ambient
				"uniform vec4 materialDif;"+ //material diffuse
				"uniform vec4 materialSpe;"+ //material specular
				"uniform float materialShi;"+ //material shininess
				"vec3 normal;"+ // world space normal for this vertex shader
				"vec3 halfVector;"+ // half-vector for Blinn-Phong
				"vec4 diffuseColor;"+
				"vec4 ambientColor;"+
				"vec4 ambientColorGlobal;"+
				"vec4 specularColor;"+
				"vec3 lightDirWorld;"+
				"float NdotL;"+ //angle between world space normal and light direction
				"float NdotHV;"+ //cos angle between half vector and normal
				"void main()"+
				"{"+
				//		"    //convert normal from model space to world space"+
				//		"    normal = normalize((gl_ModelViewMatrix * vec4(gl_Normal, 0.0)).xyz);"+
				"    normal = gl_Normal;"+ //same as line above but optimized
				"    lightDirWorld = vec4(lightDir,0.0).xyz;"+ //should pre-compute this
				//        "    normal = gl_Normal;"+
				//		""+
				//		"    //compute cos of angle between normal and light direction (world space)"+
				//		"    //that is the dot product of the two vectors. clamp result to [0,1]."+
				"    NdotL = max(dot(normal,lightDirWorld), 0.0);"+
				//		""+
				//		"    //result diffuse color from material diffuse and light diffuse colors"+
				"    diffuseColor = materialDif * lightDif;"+
				//		""+
				//		"    //result ambient color from material ambient and light ambient colors"+
				"    ambientColor = materialAmb * lightAmb;"+
				//		""+
				//		"    //result global ambient color from material ambient and global ambient color"+
				"    ambientColorGlobal = materialAmb * globalAmb;"+
				//		""+
				//		"    //half vector for specular term"+
				"    halfVector = lightDirWorld + normalize(cameraPos-gl_Vertex.xyz);"+
				//		""+
				//      "    //computer specular term - blinn-phong"+
				"    if(NdotL > 0.0)"+
				"    {"+
				"        NdotHV = max(dot(normal,halfVector),0.0);"+
				"        specularColor = materialSpe * lightSpe * pow(NdotHV,materialShi);"+
				"    }"+
				"    else {"+
				"        specularColor= vec4(0,0,0,0);"+
				"    }"+
				//		""+
				//		"    //diffuse term + ambient term + global ambient + specular term"+
				"    gl_FrontColor = NdotL * diffuseColor + ambientColor + ambientColorGlobal + specularColor;"+
				//"    gl_FrontColor = specularColor;"+
				//		""+
				//		"	 gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix * gl_Vertex;"+ //convert position from model to projected space
				"	 gl_Position = ftransform();"+ //same as line above but optimized
				"    gl_TexCoord[0] = gl_MultiTexCoord0;"+
				"}"
	};

	public static String[] shaderF = {
		"uniform sampler2D texture1;"+
		"void main()"+
				"{"+
				"	 gl_FragColor = gl_Color * texture2D(texture1, gl_TexCoord[0].st);"+
				"}"
	};
	public static int shaderVId;
	public static int shaderFId;
	public static int shaderProgramId;
	
	/**
	 * Textures
	 */
	static TextureData texData;
	static Texture tex;

	public static void main(String[] args)
	{
		//create windowing system - Java AWT
		GvWindowSystemAWTGL windowSystem = new GvWindowSystemAWTGL();

		//create renderer to use - OpenGL 3x
		GvRendererGL2 gvRenderer = new GvRendererGL2(
				windowSystem,
				"Test VBO",
				640,
				480);
		
		//test drawing cube
		initVertexData();

		//test draw obj file
		//initObj();

		gvRenderer.start();

	}

	private static void initObj() {
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\teapot\\teapot2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\dragon\\dragon2.obj";
		//String path = "C:\\Users\\yong\\GroViE\\objimport\\examples\\loadobj\\data\\sponza.obj";
		//String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";
		//String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/sponza.obj";
		GvGeometry geom = new GvGeometry();
		GvImporterObj.load(path, geom);

		indices = geom.getIndices();
		vertices = geom.getVertices();
		normals = geom.getNormals();
		System.out.println("Polygon count: " + indices.length/3);
	}

	public static void reshape( GL2 gl2, int width, int height,
			GvRenderer lRenderer) {

		gl2.glViewport(0, 0, width, height);
		float aspectRatio = (float)width / (float)height;

		//set aspect ratio into camera instance
		if(lRenderer.getRendererStateMachine().setState(
				GvRendererState.CAMERA_ASPECT_CHANGE)
				)
		{
			lRenderer.getRendererStateMachine().cameraSetAspect(aspectRatio);
			lRenderer.getRendererStateMachine().setState(GvRendererState.IDLE);
		}
		
		lRenderer.getRendererStateMachine().getCamera(cameraInstance);
		
		/* Setup the view of the cube. */
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective( cameraInstance.lFov,
				cameraInstance.lAspect,
				cameraInstance.lNear,
				cameraInstance.lFar	
				);
	}

	public static void init( GL2 gl2, GvRenderer renderer) {
		initTexture(gl2);
		initGL(gl2,renderer);
		initShaders(gl2);
		initVBOs(gl2);
		
	}

	private static void initTexture(GL2 gl2) {
		// Load earth texture.
        try {
            InputStream stream = new FileInputStream("/Users/yongzhiong/Downloads/test.jpg");
            texData = TextureIO.newTextureData(gl2.getGLProfile(),stream, false, "jpg");
            tex = TextureIO.newTexture(texData);
        }
        catch (Exception exc) {
            exc.printStackTrace();
            System.exit(1);
        }
	}
	
	public static GvGeometryTex getTube(float radius, float length, float detailDegrees, float detailLength)
	{
		int numVerticesBase = (int)(360.0f / detailDegrees);
		int numLayers = (int)(length / detailLength);
		int numVertices = numVerticesBase * (numLayers+1);
		int sizeVertices = numVertices * 3;
		int sizeIndices = numLayers * ((numVerticesBase+1)*2); //each vertical layer of tube drawn as a triangle strip
		
		double detailRadians = detailDegrees/180.0f * Math.PI;
		
		float[] vertices = new float[sizeVertices];
		float[] normals = new float[sizeVertices];
		int[] indices = new int[sizeIndices];
		float[] uv = new float[numVertices * 2];
		
		//compute base coordinates
		for(int i=0; i<numVerticesBase; ++i)
		{
			int indexOffset = i*3;
			int indexOffset1 = indexOffset+1;
			int indexOffset2 = indexOffset1+1;
			
			double angle = i * detailRadians;

			normals[indexOffset] = (float)Math.cos(angle);
			normals[indexOffset1] = 0;
			normals[indexOffset2] = (float)Math.sin(angle);
			
			vertices[indexOffset] = (float) (radius * normals[indexOffset]);
			vertices[indexOffset1] = 0;
			vertices[indexOffset2] = (float) (radius * normals[indexOffset2]);
		}
		
		//compute coordinates of vertex layers above base
		for(int j=1; j< numLayers+1; ++j)
		{
			int indexOffsetLayer = j*(numVerticesBase*3);
			
			for(int i=0; i<numVerticesBase; ++i)
			{
				int indexOffset = i*3;
				int indexOffset2 = indexOffset+2;
				int indexOffsetFinal = indexOffsetLayer + (i*3);
				int indexOffsetFinal1 = indexOffsetFinal+1;
				int indexOffsetFinal2 = indexOffsetFinal+2;

				normals[indexOffsetFinal] = normals[indexOffset];
				normals[indexOffsetFinal1] = 0;
				normals[indexOffsetFinal2] = normals[indexOffset2];
				
				vertices[indexOffsetFinal] = vertices[indexOffset];
				vertices[indexOffsetFinal1] = j * detailLength;
				vertices[indexOffsetFinal2] = vertices[indexOffset2];
			}
		}
		
		//indices
		for(int i=0; i<numLayers; ++i)
		{
			int indexOffset = i * ((numVerticesBase+1)*2);
			
			int vertexOffset = i*numVerticesBase;
			
			for(int j=0; j<numVerticesBase; ++j)
			{
				int indexOffsetInLayer = indexOffset + (j*2);
				int vertexOffsetInLayer = vertexOffset + j;
				
				indices[indexOffsetInLayer] = vertexOffsetInLayer;
				indices[indexOffsetInLayer+1] = vertexOffsetInLayer + numVerticesBase;
			}
			indices[indexOffset+(numVerticesBase*2)] = vertexOffset;
			indices[indexOffset+(numVerticesBase*2)+1] = vertexOffset + numVerticesBase;
		}
		
		//uv
		float circum = (float) (Math.PI * radius * 2);
		float arcDist = circum/numVerticesBase;
		for(int layer=0; layer<numLayers+1; ++layer)
		{
			int indexOffsetLayer = layer * numVerticesBase * 2;
			
			for(int point=0; point<numVerticesBase; ++point)
			{
				float uvx = point * arcDist;
				float uvy = layer * detailLength;
				
				int indexOffsetPoint = indexOffsetLayer + (point * 2);
				
				uv[indexOffsetPoint] = uvx;
				uv[indexOffsetPoint+1] = uvy;
			}
		}
		
		GvGeometryTex geom = new GvGeometryTex(vertices, normals, indices, uv);
		return geom;
	}

	public static GvGeometryTex getTexturedBox()
	{
		vertices = new float[72];
		normals = new float[72];
		indices = new int[36];
		uv = new float[48];

		//init normals
		float norm[] = {0,0,0};
		for (int i=0; i< 6; ++i)
		{
			if(i==0){
				norm[0] = -1.0f;
				norm[1] =  0.0f;
				norm[2] =  0.0f;
			}
			if(i==1){
				norm[0] =  0.0f;
				norm[1] =  1.0f;
				norm[2] =  0.0f;
			}
			if(i==2){
				norm[0] =  1.0f;
				norm[1] =  0.0f;
				norm[2] =  0.0f;
			}
			if(i==3){
				norm[0] =  0.0f;
				norm[1] = -1.0f;
				norm[2] =  0.0f;
			}
			if(i==4){
				norm[0] =  0.0f;
				norm[1] =  0.0f;
				norm[2] = -1.0f;
			}
			if(i==5){
				norm[0] =  0.0f;
				norm[1] =  0.0f;
				norm[2] =  1.0f;
			}
			for(int j=0; j<4; ++j)
			{
				for(int k=0; k<3; ++k)
				{
					normals[(i*4*3)+(j*3)+k] = norm[k];
				}
			}
		}

		//vertex 0 - face 1
		vertices[0] = -1.0f;
		vertices[1] = -1.0f;
		vertices[2] =  1.0f;
		//vertex 1 - face 1
		vertices[3] = -1.0f;
		vertices[4] = -1.0f;
		vertices[5] = -1.0f;
		//vertex 2 - face 1
		vertices[6] = -1.0f;
		vertices[7] =  1.0f;
		vertices[8] = -1.0f;
		//vertex 3 - face 1
		vertices[9] = -1.0f;
		vertices[10] =  1.0f;
		vertices[11] =  1.0f;

		//vertex 4 - face 2
		vertices[12] = -1.0f;
		vertices[13] =  1.0f;
		vertices[14] =  1.0f;
		//vertex 5 - face 2
		vertices[15] = -1.0f;
		vertices[16] =  1.0f;
		vertices[17] = -1.0f;
		//vertex 6 - face 2
		vertices[18] =  1.0f;
		vertices[19] =  1.0f;
		vertices[20] = -1.0f;
		//vertex 7 - face 2
		vertices[21] =  1.0f;
		vertices[22] =  1.0f;
		vertices[23] =  1.0f;

		//vertex 8 - face 3
		vertices[24] =  1.0f;
		vertices[25] =  1.0f;
		vertices[26] =  1.0f;
		//vertex 9 - face 3
		vertices[27] =  1.0f;
		vertices[28] =  1.0f;
		vertices[29] = -1.0f;
		//vertex 10 - face 3
		vertices[30] =  1.0f;
		vertices[31] = -1.0f;
		vertices[32] = -1.0f;
		//vertex 11 - face 3
		vertices[33] =  1.0f;
		vertices[34] = -1.0f;
		vertices[35] =  1.0f;

		//vertex 12 - face 4
		vertices[36] =  1.0f;
		vertices[37] = -1.0f;
		vertices[38] =  1.0f;
		//vertex 13 - face 4
		vertices[39] =  1.0f;
		vertices[40] = -1.0f;
		vertices[41] = -1.0f;
		//vertex 14 - face 4
		vertices[42] = -1.0f;
		vertices[43] = -1.0f;
		vertices[44] = -1.0f;
		//vertex 15 - face 4
		vertices[45] = -1.0f;
		vertices[46] = -1.0f;
		vertices[47] =  1.0f;

		//vertex 16 - face 5
		vertices[48] =  1.0f;
		vertices[49] = -1.0f;
		vertices[50] = -1.0f;
		//vertex 17 - face 5
		vertices[51] =  1.0f;
		vertices[52] =  1.0f;
		vertices[53] = -1.0f;
		//vertex 18 - face 5
		vertices[54] = -1.0f;
		vertices[55] =  1.0f;
		vertices[56] = -1.0f;
		//vertex 19 - face 5
		vertices[57] = -1.0f;
		vertices[58] = -1.0f;
		vertices[59] = -1.0f;

		//vertex 20 - face 6
		vertices[60] =  1.0f;
		vertices[61] =  1.0f;
		vertices[62] =  1.0f;
		//vertex 21 - face 6
		vertices[63] =  1.0f;
		vertices[64] = -1.0f;
		vertices[65] =  1.0f;
		//vertex 22 - face 6
		vertices[66] = -1.0f;
		vertices[67] = -1.0f;
		vertices[68] =  1.0f;
		//vertex 23 - face 6
		vertices[69] = -1.0f;
		vertices[70] =  1.0f;
		vertices[71] =  1.0f;

		indices[0]=2;
		indices[1]=1;
		indices[2]=0;
		indices[3]=0;
		indices[4]=3;
		indices[5]=2;

		indices[6]=6;
		indices[7]=5;
		indices[8]=4;
		indices[9]=4;
		indices[10]=7;
		indices[11]=6;

		indices[12]=10;
		indices[13]=9;
		indices[14]=8;
		indices[15]=8;
		indices[16]=11;
		indices[17]=10;

		indices[18]=14;
		indices[19]=13;
		indices[20]=12;
		indices[21]=12;
		indices[22]=15;
		indices[23]=14;

		indices[24]=18;
		indices[25]=17;
		indices[26]=16;
		indices[27]=16;
		indices[28]=19;
		indices[29]=18;

		indices[30]=22;
		indices[31]=21;
		indices[32]=20;
		indices[33]=20;
		indices[34]=23;
		indices[35]=22;
		
		uv[0] = 1.0f;
		uv[1] = 0.0f;
		uv[2] = 0.0f;
		uv[3] = 0.0f;
		uv[4] = 0.0f;
		uv[5] = 1.0f;
		uv[6] = 1.0f;
		uv[7] = 1.0f;
		
		uv[8] = 1.0f;
		uv[9] = 0.0f;
		uv[10] = 0.0f;
		uv[11] = 0.0f;
		uv[12] = 0.0f;
		uv[13] = 1.0f;
		uv[14] = 1.0f;
		uv[15] = 1.0f;
		
		uv[16] = 1.0f;
		uv[17] = 0.0f;
		uv[18] = 0.0f;
		uv[19] = 0.0f;
		uv[20] = 0.0f;
		uv[21] = 1.0f;
		uv[22] = 1.0f;
		uv[23] = 1.0f;
		
		uv[24] = 1.0f;
		uv[25] = 0.0f;
		uv[26] = 0.0f;
		uv[27] = 0.0f;
		uv[28] = 0.0f;
		uv[29] = 1.0f;
		uv[30] = 1.0f;
		uv[31] = 1.0f;
		
		uv[32] = 1.0f;
		uv[33] = 0.0f;
		uv[34] = 0.0f;
		uv[35] = 0.0f;
		uv[36] = 0.0f;
		uv[37] = 1.0f;
		uv[38] = 1.0f;
		uv[39] = 1.0f;
		
		uv[40] = 1.0f;
		uv[41] = 0.0f;
		uv[42] = 0.0f;
		uv[43] = 0.0f;
		uv[44] = 0.0f;
		uv[45] = 1.0f;
		uv[46] = 1.0f;
		uv[47] = 1.0f;
		
		GvGeometryTex geom = new GvGeometryTex(vertices, normals, indices, uv);
		return geom;
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

		vertices = new float[72];
		normals = new float[72];
		indices = new int[36];
		uv = new float[48];

		//init normals
		float norm[] = {0,0,0};
		for (int i=0; i< 6; ++i)
		{
			if(i==0){
				norm[0] = -1.0f;
				norm[1] =  0.0f;
				norm[2] =  0.0f;
			}
			if(i==1){
				norm[0] =  0.0f;
				norm[1] =  1.0f;
				norm[2] =  0.0f;
			}
			if(i==2){
				norm[0] =  1.0f;
				norm[1] =  0.0f;
				norm[2] =  0.0f;
			}
			if(i==3){
				norm[0] =  0.0f;
				norm[1] = -1.0f;
				norm[2] =  0.0f;
			}
			if(i==4){
				norm[0] =  0.0f;
				norm[1] =  0.0f;
				norm[2] = -1.0f;
			}
			if(i==5){
				norm[0] =  0.0f;
				norm[1] =  0.0f;
				norm[2] =  1.0f;
			}
			for(int j=0; j<4; ++j)
			{
				for(int k=0; k<3; ++k)
				{
					normals[(i*4*3)+(j*3)+k] = norm[k];
				}
			}
		}

		//vertex 0 - face 1
		vertices[0] = -1.0f;
		vertices[1] = -1.0f;
		vertices[2] =  1.0f;
		//vertex 1 - face 1
		vertices[3] = -1.0f;
		vertices[4] = -1.0f;
		vertices[5] = -1.0f;
		//vertex 2 - face 1
		vertices[6] = -1.0f;
		vertices[7] =  1.0f;
		vertices[8] = -1.0f;
		//vertex 3 - face 1
		vertices[9] = -1.0f;
		vertices[10] =  1.0f;
		vertices[11] =  1.0f;

		//vertex 4 - face 2
		vertices[12] = -1.0f;
		vertices[13] =  1.0f;
		vertices[14] =  1.0f;
		//vertex 5 - face 2
		vertices[15] = -1.0f;
		vertices[16] =  1.0f;
		vertices[17] = -1.0f;
		//vertex 6 - face 2
		vertices[18] =  1.0f;
		vertices[19] =  1.0f;
		vertices[20] = -1.0f;
		//vertex 7 - face 2
		vertices[21] =  1.0f;
		vertices[22] =  1.0f;
		vertices[23] =  1.0f;

		//vertex 8 - face 3
		vertices[24] =  1.0f;
		vertices[25] =  1.0f;
		vertices[26] =  1.0f;
		//vertex 9 - face 3
		vertices[27] =  1.0f;
		vertices[28] =  1.0f;
		vertices[29] = -1.0f;
		//vertex 10 - face 3
		vertices[30] =  1.0f;
		vertices[31] = -1.0f;
		vertices[32] = -1.0f;
		//vertex 11 - face 3
		vertices[33] =  1.0f;
		vertices[34] = -1.0f;
		vertices[35] =  1.0f;

		//vertex 12 - face 4
		vertices[36] =  1.0f;
		vertices[37] = -1.0f;
		vertices[38] =  1.0f;
		//vertex 13 - face 4
		vertices[39] =  1.0f;
		vertices[40] = -1.0f;
		vertices[41] = -1.0f;
		//vertex 14 - face 4
		vertices[42] = -1.0f;
		vertices[43] = -1.0f;
		vertices[44] = -1.0f;
		//vertex 15 - face 4
		vertices[45] = -1.0f;
		vertices[46] = -1.0f;
		vertices[47] =  1.0f;

		//vertex 16 - face 5
		vertices[48] =  1.0f;
		vertices[49] = -1.0f;
		vertices[50] = -1.0f;
		//vertex 17 - face 5
		vertices[51] =  1.0f;
		vertices[52] =  1.0f;
		vertices[53] = -1.0f;
		//vertex 18 - face 5
		vertices[54] = -1.0f;
		vertices[55] =  1.0f;
		vertices[56] = -1.0f;
		//vertex 19 - face 5
		vertices[57] = -1.0f;
		vertices[58] = -1.0f;
		vertices[59] = -1.0f;

		//vertex 20 - face 6
		vertices[60] =  1.0f;
		vertices[61] =  1.0f;
		vertices[62] =  1.0f;
		//vertex 21 - face 6
		vertices[63] =  1.0f;
		vertices[64] = -1.0f;
		vertices[65] =  1.0f;
		//vertex 22 - face 6
		vertices[66] = -1.0f;
		vertices[67] = -1.0f;
		vertices[68] =  1.0f;
		//vertex 23 - face 6
		vertices[69] = -1.0f;
		vertices[70] =  1.0f;
		vertices[71] =  1.0f;

		indices[0]=0;
		indices[1]=1;
		indices[2]=2;
		indices[3]=2;
		indices[4]=3;
		indices[5]=0;

		indices[6]=4;
		indices[7]=5;
		indices[8]=6;
		indices[9]=6;
		indices[10]=7;
		indices[11]=4;

		indices[12]=8;
		indices[13]=9;
		indices[14]=10;
		indices[15]=10;
		indices[16]=11;
		indices[17]=8;

		indices[18]=12;
		indices[19]=13;
		indices[20]=14;
		indices[21]=14;
		indices[22]=15;
		indices[23]=12;

		indices[24]=16;
		indices[25]=17;
		indices[26]=18;
		indices[27]=18;
		indices[28]=19;
		indices[29]=16;

		indices[30]=20;
		indices[31]=21;
		indices[32]=22;
		indices[33]=22;
		indices[34]=23;
		indices[35]=20;
		
		uv[0] = 1.0f;
		uv[1] = 0.0f;
		uv[2] = 0.0f;
		uv[3] = 0.0f;
		uv[4] = 0.0f;
		uv[5] = 1.0f;
		uv[6] = 1.0f;
		uv[7] = 1.0f;
		
		uv[8] = 1.0f;
		uv[9] = 0.0f;
		uv[10] = 0.0f;
		uv[11] = 0.0f;
		uv[12] = 0.0f;
		uv[13] = 1.0f;
		uv[14] = 1.0f;
		uv[15] = 1.0f;
		
		uv[16] = 1.0f;
		uv[17] = 0.0f;
		uv[18] = 0.0f;
		uv[19] = 0.0f;
		uv[20] = 0.0f;
		uv[21] = 1.0f;
		uv[22] = 1.0f;
		uv[23] = 1.0f;
		
		uv[24] = 1.0f;
		uv[25] = 0.0f;
		uv[26] = 0.0f;
		uv[27] = 0.0f;
		uv[28] = 0.0f;
		uv[29] = 1.0f;
		uv[30] = 1.0f;
		uv[31] = 1.0f;
		
		uv[32] = 1.0f;
		uv[33] = 0.0f;
		uv[34] = 0.0f;
		uv[35] = 0.0f;
		uv[36] = 0.0f;
		uv[37] = 1.0f;
		uv[38] = 1.0f;
		uv[39] = 1.0f;
		
		uv[40] = 1.0f;
		uv[41] = 0.0f;
		uv[42] = 0.0f;
		uv[43] = 0.0f;
		uv[44] = 0.0f;
		uv[45] = 1.0f;
		uv[46] = 1.0f;
		uv[47] = 1.0f;
		//		System.out.println("vertices for VBO");
		//		printArray(vertices);
		//		System.out.println("normals for VBO");
		//		printArray(normals);
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

		//set values of variables in shader program
		initShaderVariables(gl2);

		printLog(gl2,shaderVId);
		printLog(gl2,shaderFId);
		printLog(gl2,shaderProgramId);
	}

	private static void initShaderVariables(GL2 gl2) {
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
		int idMaterialAmbi = gl2.glGetUniformLocation(shaderProgramId,"materialAmb");
		int idMaterialDiff = gl2.glGetUniformLocation(shaderProgramId,"materialDif");
		int idMaterialSpec = gl2.glGetUniformLocation(shaderProgramId,"materialSpe");
		int idMaterialShin = gl2.glGetUniformLocation(shaderProgramId,"materialShi");
		gl2.glUniform4f(idMaterialAmbi,0.1f,0.1f,0.1f,1.0f);
		gl2.glUniform4f(idMaterialDiff,0.6f,0.6f,0.6f,1.0f);
		gl2.glUniform4f(idMaterialSpec,0.2f,0.2f,0.2f,1.0f);
		gl2.glUniform1f(idMaterialShin, 0.01f);

		//4. global ambient
		int idGlobalAmbi = gl2.glGetUniformLocation(shaderProgramId,"globalAmbi");
		gl2.glUniform4f(idGlobalAmbi,0.1f,0.1f,0.1f,1.0f);

		//5. camera position
		int idCameraPos = gl2.glGetUniformLocation(shaderProgramId,"cameraPos");
		gl2.glUniform3f(idCameraPos,cameraPosition[0],cameraPosition[1],cameraPosition[2]);
		
		//6. texture
		int idTexture = gl2.glGetUniformLocation(shaderProgramId, "texture1");
		gl2.glUniform1i(idTexture,0);
	}

	private static void initVBOs(GL2 gl2) {

		//generate VAO
		vaoId = new int[1];
		IntBuffer vaoIdBuffer = IntBuffer.wrap(vaoId);
		gl2.glGenVertexArrays(1, vaoIdBuffer); // Create our Vertex Array Object
		gl2.glBindVertexArray(vaoId[0]); // Bind our Vertex Array Object so we can use it  
		
		//generate VBO
		vboId = new int[1];
		IntBuffer vboIdBuffer = IntBuffer.wrap(vboId);
		gl2.glGenBuffers(1, vboIdBuffer);

		//bind VBO
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId[0]);

		//set total size of buffer (allocate)
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, //type of buffer
				(vertices.length * 4)+(normals.length * 4)+(uv.length * 4), //size in bytes of buffer
				null, //no data to be copied into VBO at this moment
				GL2.GL_STATIC_DRAW //buffer usage hint
				);

		//copy vertex data into VBO
		FloatBuffer verticesBuffer = FloatBuffer.wrap(vertices);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices.length*4, verticesBuffer);

		//copy normals data into VBO
		FloatBuffer normalsBuffer = FloatBuffer.wrap(normals);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vertices.length*4, normals.length*4, normalsBuffer);
		
		//copy uv data into VBO
		FloatBuffer uvBuffer = FloatBuffer.wrap(uv);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vertices.length*4 + normals.length*4, uv.length*4, uvBuffer);

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

		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY); 
		gl2.glNormalPointer(GL2.GL_FLOAT, 0, vertices.length*4);
		gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY); 
		gl2.glTexCoordPointer(2,GL2.GL_FLOAT, 0, vertices.length*4 + normals.length*4);
		
		gl2.glBindVertexArray(0); // Disable VAO 
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0); //Disable VBO
	}

	private static void initGL(GL2 gl2, GvRenderer renderer)
	{
		gl2.glDisable(GL2.GL_LIGHTING);

		/* Use depth buffering for hidden surface elimination. */
		gl2.glEnable(GL2.GL_DEPTH_TEST);

		glu = new GLU();
		glut = new GLUT();

		renderer.getRendererStateMachine().getCamera(cameraInstance);
		
		/* Setup the view of the cube. */
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();
		glu.gluPerspective( cameraInstance.lFov,
				cameraInstance.lAspect,
				cameraInstance.lNear,
				cameraInstance.lFar	
				);
	}

	public static void render( GLAutoDrawable glAutoDrawable, GL2 gl2, int width, int height , GvRenderer renderer) {
		
		long frameStart = System.nanoTime();
		
		gl2.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

		//set camera
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		renderer.getRendererStateMachine().getCamera(cameraInstance);
		glu.gluLookAt(cameraInstance.lPosition[0],cameraInstance.lPosition[1],cameraInstance.lPosition[2],  /* eye is at (0,0,5) */
				cameraInstance.lPosition[0]+cameraInstance.lView[0],
				cameraInstance.lPosition[1]+cameraInstance.lView[1],      /* center is at (0,0,0) */
				cameraInstance.lPosition[2]+cameraInstance.lView[2],
				cameraInstance.lUp[0], cameraInstance.lUp[1],cameraInstance.lUp[2]);      /* up is in positive Y direction */

		gl2.glUseProgram(shaderProgramId); 
		int idCameraPos = gl2.glGetUniformLocation(shaderProgramId,"cameraPos");
		gl2.glUniform3f(idCameraPos,cameraInstance.lPosition[0],cameraInstance.lPosition[1],cameraInstance.lPosition[2]);
		
		//draw objects
		//drawBoxStandard(gl2);
		//drawBoxVBO(gl2);
		drawBoxVBOVAO(gl2);
		//drawObjVBO(gl2);
		//drawObjVBOVAO(gl2);
		
		//draw text
		gl2.glWindowPos2i(5, 5);
		gl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frame time: " + frameTime);
		gl2.glWindowPos2i(5, 15);
		gl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
		glut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "FPS: " + 1.0/frameTime);
		
		//swap draw buffers
		glAutoDrawable.swapBuffers();
		
		//update frame time
		frameTime = (double)((System.nanoTime()-frameStart) / 1000000000.0);
		
		
	}

	private static void	drawBoxStandard(GL2 gl2)
	{
		// Adjust cube position to be asthetic angle.
		gl2.glTranslatef(0.0f, 0.0f, -1.0f);
		gl2.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
		gl2.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);

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

	private static void drawBoxVBOVAO(GL2 gl2)
	{
		// Adjust cube position to be asthetic angle.
		tex.enable(gl2);
		tex.bind(gl2);
		
		gl2.glBindVertexArray(vaoId[0]);
		
		gl2.glDrawElements(
				GL2.GL_TRIANGLES,      // mode
				indices.length,    // count
				GL2.GL_UNSIGNED_INT,   // type
				0           // element array buffer offset
				);
		
		gl2.glBindVertexArray(0);
	}
	
	private static void drawBoxVBO(GL2 gl2)
	{
		// Adjust cube position to be asthetic angle.
		gl2.glTranslatef(0.0f, 0.0f, -1.0f);
		gl2.glRotatef(60.0f, 1.0f, 0.0f, 0.0f);
		gl2.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);

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

	private static void drawObjStandard(GL2 gl2)
	{
		for(int i=0; i<indices.length; ++i)
		{

		}
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
	
	private static void drawObjVBOVAO(GL2 gl2)
	{
		gl2.glBindVertexArray(vaoId[0]);
		
		gl2.glDrawElements(
				GL2.GL_TRIANGLES,      // mode
				indices.length,    // count
				GL2.GL_UNSIGNED_INT,   // type
				0           // element array buffer offset
				);

		gl2.glBindVertexArray(0);
	}

	private static void printArray(float[] arr)
	{
		for(int i=0; i<arr.length; ++i)
		{
			System.out.println(arr[i]);
		}
	}

	private static void printArray(int[] arr)
	{
		for(int i=0; i<arr.length; ++i)
		{
			System.out.println(arr[i]);
		}
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

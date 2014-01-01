package de.grovie.renderer.GL2;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.Animator;
import com.jogamp.opengl.util.gl2.GLUT;

import de.grovie.data.importer.obj.GvImporterObj;
import de.grovie.data.object.GvGeometry;
import de.grovie.exception.GvExRendererPassShaderResource;
import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvDrawGroup;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvIllustrator;
import de.grovie.renderer.GvIndexBuffer;
import de.grovie.renderer.GvMaterial;
import de.grovie.renderer.GvPass;
import de.grovie.renderer.GvPrimitive;
import de.grovie.renderer.GvVertexArray;
import de.grovie.renderer.GvVertexBuffer;

public class GvIllustratorGL2  extends GvIllustrator implements GLEventListener{
	
	GLAutoDrawable lglAutoDrawable;
	GL2 lgl2;
	GLU lglu;
	GLUT lglut;
	
	GvPassGL2 lPass;
	GvVertexArray lVao;
	int indices[];
	
	public GvIllustratorGL2(GvRendererGL2 renderer)
	{
		super(renderer);
		
		lglAutoDrawable = null;
		lgl2 = null;
	}
	
	@Override
	public void display(GLAutoDrawable glAutoDrawable) {
		//display();
		lPass.start();
		GL2 gl2 = glAutoDrawable.getGL().getGL2();
		
		gl2.glBindVertexArray(lVao.getId());
		
		gl2.glDrawElements(
				GL2.GL_TRIANGLES,      // mode
				indices.length,    // count
				GL2.GL_UNSIGNED_INT,   // type
				0           // element array buffer offset
				);

		gl2.glBindVertexArray(0);
		lPass.stop();
		
		//swap draw buffers
				lglAutoDrawable.swapBuffers();		
	}
	
	@Override
	public void init(GLAutoDrawable glAutoDrawable) {
		lglAutoDrawable = glAutoDrawable;
		lgl2 = glAutoDrawable.getGL().getGL2();
		lglu = new GLU();
		lglut = new GLUT();
		
		//glAutoDrawable.getGL().setSwapInterval(1); //uncomment this line for v-sync
		Animator animator = (Animator)lRenderer.getAnimator();
		animator.add(glAutoDrawable);
		animator.setRunAsFastAsPossible(true);
		animator.start();
		
		try {
			init();
		} catch (GvExRendererPassShaderResource e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
		reshape(x,y,width,height);
	}

	@Override
	public void dispose(GLAutoDrawable glAutoDrawable) {
		dispose();
	}

	@Override
	public void reshape(int x, int y, int width, int height) {
		lPipeline.reshape(x,y,width,height);
	}

	@Override
	public void init() throws GvExRendererPassShaderResource {
		//disable JOGL auto buffer swap to allow timing frame draw
		lglAutoDrawable.setAutoSwapBufferMode(false);
		
		//check if multi-color attachment FBOs are supported. assign pipeline
		//if(lglAutoDrawable.getContext().hasFullFBOSupport())
		//	lPipeline = new GvPipelineGL2Deferred(lRenderer, lglAutoDrawable, lgl2, lglu);
		//else
			lPipeline = new GvPipelineGL2(lRenderer, lglAutoDrawable, lgl2, lglu);
			
		//FOR DEBUG
		try{
			GvRendererGL2 rendererGL2 = (GvRendererGL2)lRenderer;
			GvDrawGroup drawGrpUpdate = rendererGL2.getDrawGroupUpdate();
			
			//clear update buffers
//			drawGrpUpdate.clear(rendererGL2);
			
			//add test material
			rendererGL2.addMaterial(new GvMaterial());
			//add test texture
//			InputStream stream = new FileInputStream("/Users/yongzhiong/Downloads/test.jpg");
//			rendererGL2.addTexture2D((GvTexture2DGL2)lRenderer.getDevice().createTexture2D(stream, "jpg"));
			//init draw groups
//			rendererGL2.initDrawGroups();
			//prepare test geom
			String path = "/Users/yongzhiong/GroViE/objimport_1_1_2/objimport/examples/loadobj/data/spheres.obj";		
			GvGeometry geom = new GvGeometry();
			GvImporterObj.load(path, geom);
			indices = geom.getIndices();
			float vertices[] = geom.getVertices();
			float normals[] = geom.getNormals();
			
			//FOR DEBUG
			GvContext context = rendererGL2.getContext();
			GvDevice device = rendererGL2.getDevice();
			GvVertexArray vao = new GvVertexArray(-1);
			lVao = context.createVertexArray(vao);
			lgl2.glBindVertexArray(vao.getId());
			
			GvVertexBuffer vbo = device.createVertexBuffer((vertices.length * 4)+(normals.length * 4));
			lgl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.getId());

			//set total size of buffer (allocate)
			lgl2.glBufferData(GL2.GL_ARRAY_BUFFER, //type of buffer
					(vertices.length * 4)+(normals.length * 4), //size in bytes of buffer
					null, //no data to be copied into VBO at this moment
					GL2.GL_STATIC_DRAW //buffer usage hint
					);

			//copy vertex data into VBO
			FloatBuffer verticesBuffer = FloatBuffer.wrap(vertices);
			lgl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, vertices.length*4, verticesBuffer);

			//copy normals data into VBO
			FloatBuffer normalsBuffer = FloatBuffer.wrap(normals);
			lgl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vertices.length*4, normals.length*4, normalsBuffer);

			GvIndexBuffer ibo = device.createIndexBuffer(indices.length * 4);
			
			lgl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo.getId());

			//set total size of buffer (allocate) and copy indices into it
			IntBuffer indicesBuffer = IntBuffer.wrap(indices);
			lgl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, //type of buffer
					(indices.length * 4), //size in bytes of buffer
					indicesBuffer, //no data to be copied into VBO at this moment
					GL2.GL_STATIC_DRAW //buffer usage hint
					);

			lgl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
			lgl2.glVertexPointer(3, GL2.GL_FLOAT, 0, 0);
			lgl2.glEnableClientState(GL2.GL_NORMAL_ARRAY); 
			lgl2.glNormalPointer(GL2.GL_FLOAT, 0, vertices.length*4);
			
			lgl2.glBindVertexArray(0); // Disable VAO 
			lgl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0); //Disable VBO
			
			lPass = (GvPassGL2)lPipeline.getPass();
			
			//END DEBUG
			
//			//send geometry to categorized draw groups //TODO: discard unnecessary listing of geometry in buffer sets
//			GvBufferSet bufferSet = drawGrpUpdate.getBufferSet(false, -1, 0, GvPrimitive.PRIMITIVE_TRIANGLE, true);
//			bufferSet.insertGeometry(vertices, normals, indices);
//		
//			//send geom to hardware buffers - simulate action by foreign thread
//			//TODO: integrate this with previous geometry insertion step
//			drawGrpUpdate.update(rendererGL2);
//			
//			//update VAOs - rendering thread action, action after receiving msg to swap VBOs
//			drawGrpUpdate.updateVAO(rendererGL2);
//			
			//swap buffers - rendering thread action, action after receiving msg to swap VBOs
			rendererGL2.swapBuffers();
			
		}
		catch(Exception e)
		{
			System.out.println("test fail");
			System.out.println(e.getMessage());
		}
		
		//END DEBUG
	}
	
	@Override
	public void display2DOverlay() {
		if(lRenderer.getRendererStateMachine().getOverlayOn())
		{
			lgl2.glWindowPos2i(5, 5);
			lgl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "Frame time: " + lFrameTime);
			lgl2.glWindowPos2i(5, 15);
			lgl2.glColor4f(0.0f, 0.0f, 1.0f, 1.0f);
			lglut.glutBitmapString(GLUT.BITMAP_HELVETICA_10, "FPS: " + 1.0/lFrameTime);
		}
	}
	
	/**
	 * Invoked after display3D() and display2DOverlay()
	 */
	@Override
	public void displayEnd() {
		//swap draw buffers
		lglAutoDrawable.swapBuffers();		
	}

	@Override
	public void dispose() {
		// TODO: Call upon engine shutdown. delete allocated memory where possible.
	}

	public GLAutoDrawable getGLAutoDrawable() {
		return lglAutoDrawable;
	}

	public void setGLAutoDrawable(GLAutoDrawable glAutoDrawable) {
		this.lglAutoDrawable = glAutoDrawable;
	}

	public GL2 getGL2() {
		return lgl2;
	}

	public void setGL2(GL2 gl2) {
		this.lgl2 = gl2;
	}

	@Override
	public void processMessages() {
		// TODO check message queues and process messages
		
	}
}

package de.grovie.renderer.GL2;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import de.grovie.exception.GvExRendererIndexBuffer;
import de.grovie.exception.GvExRendererVertexArray;
import de.grovie.exception.GvExRendererVertexBuffer;
import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;
import de.grovie.renderer.GvIndexBuffer;
import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.GvVertexArray;
import de.grovie.renderer.GvVertexBuffer;

public class GvBufferSetGL2 extends GvBufferSet {
	
	public GvBufferSetGL2() {
	}

	/**
	 * Inserts geometry info (vertices, normals, uv-coords) into lists
	 */
	@Override 
	protected void insertIntoArrayBuffers(float[] vertices, float[] normals, float[] uvcoords) 
	{
		insertIntoArrayBuffers(vertices, normals);
		lUv.add(uvcoords);
	}
	
	/**
	 * Inserts geometry info (vertices, normals) into lists
	 */
	@Override
	protected void insertIntoArrayBuffers(float[] vertices, float[] normals) {
		lVertices.add(vertices);
		lNormals.add(normals);
	}

	/**
	 * Inserts geometry info (indices) into lists
	 */
	@Override
	protected void insertIntoElementBuffer(int[] indices) {
		lIndices.add(indices);
	}

	/**
	 * Update hardware buffers with geometry info from lists
	 * @throws GvExRendererVertexArray 
	 * @throws GvExRendererVertexBuffer 
	 * @throws GvExRendererIndexBuffer 
	 */
	@Override
	public void update(GvRenderer renderer) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer 
	{
		
		if(lVertices.size() == 0)
			return;
		
		GvDevice device = renderer.getDevice();
		
		//TODO: check if creation of initial VBO and IBO can be outside update method.
		if(lVertexBuffers.size()==0)
			lVertexBuffers.add(device.createVertexBuffer(GvBufferSet.VBO_BLOCK_SIZE));
		
		if(lIndexBuffers.size()==0)
			lIndexBuffers.add(device.createIndexBuffer(GvBufferSet.VBO_BLOCK_SIZE));
		
		//loop through CPU-side geometry lists
		for(int i=0; i<lVertices.size(); ++i)
		{
			//send geometry to GPU
			update(lVertices.get(i),
					lNormals.get(i),
					(lUv.size()==0)?null:lUv.get(i),
					lIndices.get(i),
					renderer);
		}
	}
	
	private void update(float[] vertices, float[] normals, float[] uv, int[] indices, GvRenderer renderer) 
			throws GvExRendererVertexBuffer, GvExRendererIndexBuffer
	{
		GvIllustratorGL2 illustrator = (GvIllustratorGL2)renderer.getIllustrator();
		GL2 gl2 = illustrator.getGL2();
		GvDevice device = renderer.getDevice();
		
		//ensure current vbo has sufficient memory for vertex,normal and uv list
		GvVertexBuffer vbo = sizeCheckVertexBuffer(vertices,normals,uv, device);
		
		//ensure current ibo has sufficient memory for polygon indices
		GvIndexBuffer ibo = sizeCheckIndexBuffer(indices, device);
		
		//remember offsets
		int vboIndex = lVertexBuffers.size()-1;
		long vboOffset = vbo.getSizeUsed();
		int iboIndex = lIndexBuffers.size()-1;
		long iboOffset = ibo.getSizeUsed();
		
		//bind last vbo
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.getId());
		
		//copy vertex coords to vbo
		FloatBuffer verticesBuffer = FloatBuffer.wrap(vertices);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vboOffset, vertices.length*4, verticesBuffer);
		
		//copy normals to vbo
		FloatBuffer normalsBuffer = FloatBuffer.wrap(normals);
		gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vboOffset + vertices.length*4, normals.length*4, normalsBuffer);
		
		//copy uv coordinates to vbo
		if(uv!=null)
		{
			FloatBuffer uvBuffer = FloatBuffer.wrap(uv);
			gl2.glBufferSubData(GL2.GL_ARRAY_BUFFER, vboOffset +  vertices.length*4 + normals.length*4, uv.length*4, uvBuffer);
		}
		
		//bind last ibo
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo.getId());
		
		//copy indices to ibo
		IntBuffer indicesBuffer = IntBuffer.wrap(indices);
		gl2.glBufferSubData(GL2.GL_ELEMENT_ARRAY_BUFFER, iboOffset, indices.length*4, indicesBuffer);
		
		//create container to remember index of vbo,ibo and offsets for 
		//creation of VAO at renderer thread later
		GvVertexArray vao = new GvVertexArray(-1);
		vao.setVboIndex(vboIndex);	//used in VAO init
		vao.setVboOffset(vboOffset);//used in VAO init
		vao.setIboIndex(iboIndex);	//used in VAO init
		vao.setIboOffset(iboOffset);//used in draw call
		vao.setSizeVertices(vertices.length * 4);
		vao.setSizeNormals(normals.length * 4);
		if(uv!=null)
			vao.setSizeUv(uv.length * 4);
		else
			vao.setSizeUv(0);
		vao.setSizeIndices(indices.length * 4);
		
		//create VAO object (only wrapper object at CPU side, not allocated at GPU yet)
		lVertexArrays.add(vao);
		
		//update memory usage of vbo and ibo wrapper objects
		vbo.setSizeUsed(vbo.getSizeUsed()+vao.getSizeVertices()+vao.getSizeNormals()+vao.getSizeUv());
		ibo.setSizeUsed(ibo.getSizeUsed()+vao.getSizeIndices());
		
	}
	
	private GvVertexBuffer sizeCheckVertexBuffer(float[] vertices, float[] normals, float[] uv, GvDevice device) 
			throws GvExRendererVertexBuffer
	{
		long sizeRequired = vertices.length * 4 + 
				normals.length * 4 + 
				((uv==null)?0:uv.length * 4);
		
		//size of geometry exceeds usual allocated block size
		//allocate custom size vbo
		if(sizeRequired > GvBufferSet.VBO_BLOCK_SIZE)
			this.lVertexBuffers.add(device.createVertexBuffer(sizeRequired));
		
		GvVertexBuffer vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		if(vbo.getSizeFree() < sizeRequired)
		{
			this.lVertexBuffers.add(device.createVertexBuffer(GvBufferSet.VBO_BLOCK_SIZE));
			vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		}
		
		return vbo;
	}
	
	private GvIndexBuffer sizeCheckIndexBuffer(int[] indices, GvDevice device) throws GvExRendererIndexBuffer 
	{
		long sizeRequired = indices.length * 4;
		
		//size of geometry exceeds usual allocated block size
		//allocate custom size vbo
		if(sizeRequired > GvBufferSet.IBO_BLOCK_SIZE)
			this.lIndexBuffers.add(device.createIndexBuffer(sizeRequired));
		
		GvIndexBuffer ibo = lIndexBuffers.get(lIndexBuffers.size()-1);
		if(ibo.getSizeFree() < sizeRequired)
		{
			this.lIndexBuffers.add(device.createIndexBuffer(GvBufferSet.IBO_BLOCK_SIZE));
			ibo = lIndexBuffers.get(lIndexBuffers.size()-1);
		}
		
		return ibo;
	}
	
	@Override
	public void clear(GvRenderer renderer)
	{
		GvIllustratorGL2 illustrator = (GvIllustratorGL2)renderer.getIllustrator();
		GL2 gl2 = illustrator.getGL2();
		
		//clear geometry data lists
		lVertices.clear();
		lNormals.clear();
		lUv.clear();
		lIndices.clear();
		
		//clear VBOs
		for(int i=0; i<lVertexBuffers.size(); ++i)
		{
			GvVertexBuffer vbo = lVertexBuffers.get(i);
			vbo.setSizeUsed(0);	//reset usage counter
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo.getId()); //bind
			
			//clear memory - TODO: check if this is necessary
			gl2.glBufferData(GL2.GL_ARRAY_BUFFER, vbo.getSize(), null, GL2.GL_STATIC_DRAW); 
		}
		
		//clear IBOs
		for(int i=0; i<lIndexBuffers.size(); ++i)
		{
			GvIndexBuffer ibo = lIndexBuffers.get(i);		
			ibo.setSizeUsed(0);	//reset usage counter
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo.getId()); //bind
			
			//clear memory - TODO: check if this is necessary
			gl2.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER, ibo.getSize(), null, GL2.GL_STATIC_DRAW);
		}
		
		//swap set of vertex arrays
		//NOTE: unable to delete them from foreign thread (VAOs are not shared across contexts)
		//      therefore, obsolete VAOs are retained and only deleted when rendering thread
		//      prepares this buffer set for rendering
		ArrayList<GvVertexArray> temp = lVertexArraysToDelete;
		lVertexArraysToDelete = lVertexArrays;
		lVertexArrays = temp;
		
		//clear vertex array list
		lVertexArrays.clear();
	}

	@Override
	public void updateVAO(GvRenderer renderer) throws GvExRendererVertexArray {
		GvIllustratorGL2 illustrator = (GvIllustratorGL2)renderer.getIllustrator();
		GL2 gl2 = illustrator.getGL2();
		
		//delete and release old VAO ids
		for(int i=0; i<this.lVertexArraysToDelete.size(); ++i)
		{
			GvVertexArray vaoObsolete = lVertexArraysToDelete.get(i);
			IntBuffer vaoId = IntBuffer.wrap(new int[]{vaoObsolete.getId()});
			gl2.glDeleteVertexArrays(1, vaoId);
		}
		
		//create new VAOs for rendering
		GvContext context = renderer.getContext();
		for(int j=0; j<this.lVertexArrays.size(); ++j)
		{
			GvVertexArray vao = lVertexArrays.get(j);
			context.createVertexArray(vao); //create opengl id for VAO
			
			//bind vao
			gl2.glBindVertexArray(vao.getId());
			
			//set VAO details
			//set buffers to bind
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, this.lVertexBuffers.get(vao.getVboIndex()).getId());
			gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, this.lIndexBuffers.get(vao.getIboIndex()).getId());
			//set client states
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
			gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, vao.getVboOffset());
			gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY); 
			gl2.glNormalPointer(GL2.GL_FLOAT, 0, vao.getVboOffset() + vao.getSizeVertices());
			if(vao.getSizeUv() > 0)
			{
				gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY); 
				gl2.glTexCoordPointer(2,GL2.GL_FLOAT, 0, vao.getVboOffset() + vao.getSizeVertices() + vao.getSizeNormals());
			}
			
			gl2.glBindVertexArray(0); // Disable VAO 
		}
	}
}

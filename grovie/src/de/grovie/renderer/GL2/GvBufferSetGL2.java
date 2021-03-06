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
	 * Update hardware buffers with geometry info from lists
	 * @throws GvExRendererVertexArray 
	 * @throws GvExRendererVertexBuffer 
	 * @throws GvExRendererIndexBuffer 
	 */
	@Override
	public void update(Object libraryAPI, GvDevice device) 
			throws GvExRendererVertexBuffer, GvExRendererVertexArray, GvExRendererIndexBuffer 
	{

		GL2 gl2 = (GL2)libraryAPI;
		GvDeviceGL2 devicegl2 = (GvDeviceGL2)device; 
		
		if(lVertices.size() == 0)
			return;

		//TODO: check if creation of initial VBO and IBO can be outside update method.
		if(lVertexBuffers.size()==0)
			lVertexBuffers.add(device.createVertexBuffer(GvBufferSet.VBO_BLOCK_SIZE,gl2));

		if(lIndexBuffers.size()==0)
			lIndexBuffers.add(device.createIndexBuffer(GvBufferSet.VBO_BLOCK_SIZE,gl2));

		//loop through CPU-side geometry lists
		for(int i=0; i<lVertices.size(); ++i)
		{
			//send geometry to GPU
			update(lVertices.get(i),
					lNormals.get(i),
					(lUv.size()==0)?null:lUv.get(i),
					lIndices.get(i),
					lMatrices.get(i),
					gl2,
					devicegl2);
		}
	}

	private void update(float[] vertices, float[] normals, float[] uv, int[] indices, float[] matrix, GL2 gl2, GvDeviceGL2 device) 
			throws GvExRendererVertexBuffer, GvExRendererIndexBuffer
	{

		//ensure current vbo has sufficient memory for vertex,normal and uv list
		GvVertexBuffer vbo = sizeCheckVertexBuffer(vertices,normals,uv, device,gl2);

		//ensure current ibo has sufficient memory for polygon indices
		GvIndexBuffer ibo = sizeCheckIndexBuffer(indices, device,gl2);

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
		GvVertexArray vao = new GvVertexArray(-1,
				vboIndex,
				vboOffset,
				iboIndex,
				iboOffset,
				vertices.length * 4,
				normals.length * 4,
				(uv==null)?0:(uv.length * 4),
				indices.length * 4,
				matrix
				);

		//create VAO object (only wrapper object at CPU side, not allocated at GPU yet)
		lVertexArrays.add(vao);

		//update memory usage of vbo and ibo wrapper objects
		vbo.setSizeUsed(vbo.getSizeUsed()+vao.getSizeVertices()+vao.getSizeNormals()+vao.getSizeUv());
		ibo.setSizeUsed(ibo.getSizeUsed()+vao.getSizeIndices());

	}

	private GvVertexBuffer sizeCheckVertexBuffer(float[] vertices, float[] normals, float[] uv, GvDevice device, GL2 gl2) 
			throws GvExRendererVertexBuffer
	{
		long sizeRequired = vertices.length * 4 + 
				normals.length * 4 + 
				((uv==null)?0:uv.length * 4);

		//size of geometry exceeds usual allocated block size
		//allocate custom size vbo
		if(sizeRequired > GvBufferSet.VBO_BLOCK_SIZE)
			this.lVertexBuffers.add(device.createVertexBuffer(sizeRequired,gl2));

		GvVertexBuffer vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		if(vbo.getSizeFree() < sizeRequired)
		{
			this.lVertexBuffers.add(device.createVertexBuffer(GvBufferSet.VBO_BLOCK_SIZE,gl2));
			vbo = lVertexBuffers.get(lVertexBuffers.size()-1);
		}

		return vbo;
	}

	private GvIndexBuffer sizeCheckIndexBuffer(int[] indices, GvDevice device, GL2 gl2) throws GvExRendererIndexBuffer 
	{
		long sizeRequired = indices.length * 4;

		//size of geometry exceeds usual allocated block size
		//allocate custom size vbo
		if(sizeRequired > GvBufferSet.IBO_BLOCK_SIZE)
			this.lIndexBuffers.add(device.createIndexBuffer(sizeRequired,gl2));

		GvIndexBuffer ibo = lIndexBuffers.get(lIndexBuffers.size()-1);
		if(ibo.getSizeFree() < sizeRequired)
		{
			this.lIndexBuffers.add(device.createIndexBuffer(GvBufferSet.IBO_BLOCK_SIZE,gl2));
			ibo = lIndexBuffers.get(lIndexBuffers.size()-1);
		}

		return ibo;
	}

	@Override
	public void clear(Object libraryAPI)
	{
		clear((GL2)libraryAPI);
	}

	private void clear(GL2 gl2)
	{
		//clear geometry data lists
		lVertices.clear();
		lNormals.clear();
		lUv.clear();
		lIndices.clear();
		lMatrices.clear();
		
		lInstanceMatrices.clear();
		lInstanceSetIndices.clear();

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
		//NOTE: Although this method is now invoked on rendering thread, the mechanism
		//      to retain vao deletion for later is maintained for future possiblities
		//      to clear VBOs on data thread and clear VAOs later on rendering thread.
		ArrayList<GvVertexArray> temp = lVertexArraysToDelete;
		lVertexArraysToDelete = lVertexArrays;
		lVertexArrays = temp;

		//clear vertex array list
		lVertexArrays.clear();
	}

	/**
	 * This method releases obsolete VAOs and creates new VAOs in openGL.
	 */
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

			updateVAOState(gl2, 
				vao.getId(), 
				this.lVertexBuffers.get(vao.getVboIndex()).getId(),
				this.lIndexBuffers.get(vao.getIboIndex()).getId(),
				vao.getVboOffset(),
				vao.getSizeVertices(),
				vao.getSizeNormals(),
				vao.getSizeUv()
				);
		}
	}
	
	/**
	 * This method sets the OpenGL client state for a vertex array object.
	 * @param gl2
	 * @param vaoId
	 * @param vboId
	 * @param iboId
	 * @param vboOffset
	 * @param sizeVertices
	 * @param sizeNormals
	 * @param sizeUv
	 */
	private void updateVAOState(GL2 gl2, 
			int vaoId, 
			int vboId, 
			int iboId, 
			long vboOffset, 
			long sizeVertices, 
			long sizeNormals, 
			long sizeUv)
	{
		//bind vao
		gl2.glBindVertexArray(vaoId);

		//set VAO details
		//set buffers to bind
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vboId);
		gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, iboId);
		//set client states
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY); 
		gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, vboOffset);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY); 
		gl2.glNormalPointer(GL2.GL_FLOAT, 0, vboOffset + sizeVertices);
		if(sizeUv > 0)
		{
			gl2.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY); 
			gl2.glTexCoordPointer(2,GL2.GL_FLOAT, 0, vboOffset + sizeVertices +sizeNormals);
		}

		gl2.glBindVertexArray(0); // Disable VAO 
	}
}

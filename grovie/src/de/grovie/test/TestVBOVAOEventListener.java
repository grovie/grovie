package de.grovie.test;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

public class TestVBOVAOEventListener implements GLEventListener{

	@Override
	public void display(GLAutoDrawable arg0) {
		
        
		GL gl = arg0.getGL();
		GL2 gl2 = gl.getGL2();
		
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );

        // draw a triangle filling the window
        gl2.glLoadIdentity();
        
		//Initialise VBO - do only once, at start of program
		//Create a variable to hold the VBO identifier
		int vbo[] = new int[1];
		IntBuffer triangleVBO = IntBuffer.wrap(vbo);
		 
		//Vertices of a triangle (counter-clockwise winding)
		float data[] = {0.0f, 0.0f, 
				//1.0f, 0.0f, 0.0f, 
				(float)arg0.getWidth(), 0.0f,
				//0.0f, 1.0f, 0.0f, 
				(float)arg0.getWidth()/2.0f, (float)arg0.getHeight()//,
				//0.0f, 0.0f, 1.0f
				};
		FloatBuffer dataBuffer = FloatBuffer.wrap(data);
		//Create a new VBO and use the variable id to store the VBO id
		gl2.glGenBuffers(1, triangleVBO);
		
		//Make the new VBO active
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vbo[0]);
		 
		//Upload vertex data to the video device
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, data.length, dataBuffer, GL2.GL_STATIC_DRAW);
		
		//Establish array contains vertices (not normals, colours, texture coords etc)
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		
		//Draw Triangle from VBO - do each time window, view point or data changes
		//Establish its 3 coordinates per vertex with zero stride in this array; necessary here
		gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, 0);
		//gl2.glColorPointer(3, GL2.GL_FLOAT, 8, 8);
		//Actually draw the triangle, giving the number of vertices provided
		gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
		 
		//Force display to be drawn now
		gl2.glFlush();
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int x, int y, int width, int height) {
		OneTriangleAWT.setup( arg0.getGL().getGL2(), width, height );
	}

}

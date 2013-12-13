package de.grovie.test.engine.renderer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import de.grovie.engine.renderer.GL3.GvRendererGL3;
import de.grovie.engine.renderer.windowsystem.AWT.GvWindowSystemAWT;

public class TestRenderer {

	// constants
	public static final int   SCREEN_WIDTH    = 400;
	public static final int   SCREEN_HEIGHT   = 300;
	public static final float CAMERA_DISTANCE = 5.0f;
	public static final int   TEXT_WIDTH      = 8;
	public static final int   TEXT_HEIGHT     = 13;
	
	float vertices[]  = { 1, 1, 1,  -1, 1, 1,  -1,-1, 1,      // v0-v1-v2 (front)
			-1,-1, 1,   1,-1, 1,   1, 1, 1,      // v2-v3-v0

			1, 1, 1,   1,-1, 1,   1,-1,-1,      // v0-v3-v4 (right)
			1,-1,-1,   1, 1,-1,   1, 1, 1,      // v4-v5-v0

			1, 1, 1,   1, 1,-1,  -1, 1,-1,      // v0-v5-v6 (top)
			-1, 1,-1,  -1, 1, 1,   1, 1, 1,      // v6-v1-v0

			-1, 1, 1,  -1, 1,-1,  -1,-1,-1,      // v1-v6-v7 (left)
			-1,-1,-1,  -1,-1, 1,  -1, 1, 1,      // v7-v2-v1

			-1,-1,-1,   1,-1,-1,   1,-1, 1,      // v7-v4-v3 (bottom)
			1,-1, 1,  -1,-1, 1,  -1,-1,-1,      // v3-v2-v7

			1,-1,-1,  -1,-1,-1,  -1, 1,-1,      // v4-v7-v6 (back)
			-1, 1,-1,   1, 1,-1,   1,-1,-1 };    // v6-v5-v4

	//normal array
	float normals[]   = { 0, 0, 1,   0, 0, 1,   0, 0, 1,      // v0-v1-v2 (front)
			0, 0, 1,   0, 0, 1,   0, 0, 1,      // v2-v3-v0

			1, 0, 0,   1, 0, 0,   1, 0, 0,      // v0-v3-v4 (right)
			1, 0, 0,   1, 0, 0,   1, 0, 0,      // v4-v5-v0

			0, 1, 0,   0, 1, 0,   0, 1, 0,      // v0-v5-v6 (top)
			0, 1, 0,   0, 1, 0,   0, 1, 0,      // v6-v1-v0

			-1, 0, 0,  -1, 0, 0,  -1, 0, 0,      // v1-v6-v7 (left)
			-1, 0, 0,  -1, 0, 0,  -1, 0, 0,      // v7-v2-v1

			0,-1, 0,   0,-1, 0,   0,-1, 0,      // v7-v4-v3 (bottom)
			0,-1, 0,   0,-1, 0,   0,-1, 0,      // v3-v2-v7

			0, 0,-1,   0, 0,-1,   0, 0,-1,      // v4-v7-v6 (back)
			0, 0,-1,   0, 0,-1,   0, 0,-1 };    // v6-v5-v4

	//color array
	float colors[]    = { 1, 1, 1,   1, 1, 0,   1, 0, 0,      // v0-v1-v2 (front)
			1, 0, 0,   1, 0, 1,   1, 1, 1,      // v2-v3-v0

			1, 1, 1,   1, 0, 1,   0, 0, 1,      // v0-v3-v4 (right)
			0, 0, 1,   0, 1, 1,   1, 1, 1,      // v4-v5-v0

			1, 1, 1,   0, 1, 1,   0, 1, 0,      // v0-v5-v6 (top)
			0, 1, 0,   1, 1, 0,   1, 1, 1,      // v6-v1-v0

			1, 1, 0,   0, 1, 0,   0, 0, 0,      // v1-v6-v7 (left)
			0, 0, 0,   1, 0, 0,   1, 1, 0,      // v7-v2-v1

			0, 0, 0,   0, 0, 1,   1, 0, 1,      // v7-v4-v3 (bottom)
			1, 0, 1,   1, 0, 0,   0, 0, 0,      // v3-v2-v7

			0, 0, 1,   0, 0, 0,   0, 1, 0,      // v4-v7-v6 (back)
			0, 1, 0,   0, 1, 1,   0, 0, 1 };    // v6-v5-v4

	public static void main(String[] args)
	{
		//create windowing system - Java AWT
		GvWindowSystemAWT windowSystem = new GvWindowSystemAWT();

		//create renderer to use - OpenGL 3x
		GvRendererGL3 gvRenderer = new GvRendererGL3(
				windowSystem,
				"Test VBO",
				SCREEN_WIDTH,
				SCREEN_HEIGHT);

		gvRenderer.start();


	}

	public static void setup( GL2 gl2, int width, int height ) {
		gl2.glMatrixMode( GL2.GL_PROJECTION );
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as the window
		GLU glu = new GLU();
		glu.gluOrtho2D( 0.0f, width, 0.0f, height );

		gl2.glMatrixMode( GL2.GL_MODELVIEW );
		gl2.glLoadIdentity();

		gl2.glViewport( 0, 0, width, height );
	}

	public static void render( GL2 gl2, int width, int height ) {
		gl2.glClear( GL.GL_COLOR_BUFFER_BIT );

		// draw a triangle filling the window
		gl2.glLoadIdentity();
		gl2.glBegin( GL.GL_TRIANGLES );
		gl2.glColor3f( 1, 0, 0 );
		gl2.glVertex2f( 0, 0 );
		gl2.glColor3f( 0, 1, 0 );
		gl2.glVertex2f( width, 0 );
		gl2.glColor3f( 0, 0, 1 );
		gl2.glVertex2f( width / 2, height );
		gl2.glEnd();
	}
	
	public static void init()
	{
		
	}
}

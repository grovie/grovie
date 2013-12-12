package de.grovie.engine.renderer.AWTGL;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.GvDevice;
import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.GvGraphicsWindow;
import de.grovie.engine.renderer.GvShaderProgram;
import de.grovie.engine.renderer.GvVertexBuffer;

public class GvDeviceAWTGL extends GvDevice{

	@Override
	public GvGraphicsWindow createWindow(int width, int height, GvEventListener listener) {
		
		//create java awt frame with GLCanvas
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		final GLCanvas glcanvas = new GLCanvas( glcapabilities );

		glcanvas.addGLEventListener((GLEventListener)listener);

		final Frame frame = new Frame( "GroViE Sandbox" );
		frame.add( glcanvas );
		frame.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				frame.remove( glcanvas );
				frame.dispose();
				System.exit( 0 );
			}
		});

		frame.setSize( 640, 480 );
		frame.setVisible( true );
		
		return new GvGraphicsWindowAWTGL(frame);
	}

	@Override
	public GvShaderProgram createShaderProgram(String vertexShaderSource,
			String geometryShaderSource, String fragmentShaderSource) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GvVertexBuffer createVertexBuffer(int sizeInBytes) {
		// TODO Auto-generated method stub
		return null;
	}

}

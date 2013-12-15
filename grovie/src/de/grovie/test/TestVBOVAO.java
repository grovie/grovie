package de.grovie.test;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

public class TestVBOVAO {

	public static void main(String[] args) {
		//create java awt frame with GLCanvas
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		final GLCanvas glcanvas = new GLCanvas( glcapabilities );

		glcanvas.addGLEventListener((GLEventListener)new TestVBOVAOEventListener());

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

	}

}

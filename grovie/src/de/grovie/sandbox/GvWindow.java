package de.grovie.sandbox;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.GvEngine;

public class GvWindow implements Runnable {

	Thread lThread;
	GvEngine lEngine;
	
	public GvWindow(GvEngine engine)
	{
		lEngine = engine;
		lThread = new Thread(this, "GroViE Sandbox Window");
		lThread.start(); // Start the thread
	}
	
	@Override
	public void run() {
		createWindowAWT();
	}

	/**
	 * Creates an AWT window with GLCanvas for openGL rendering
	 */
	private void createWindowAWT()
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		final GLCanvas glcanvas = new GLCanvas( glcapabilities );

		glcanvas.addGLEventListener((GLEventListener)lEngine.getEventListener());

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

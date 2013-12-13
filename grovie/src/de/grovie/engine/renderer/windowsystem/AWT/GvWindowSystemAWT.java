package de.grovie.engine.renderer.windowsystem.AWT;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.GvEventListener;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvWindowSystemAWT extends GvWindowSystem {
	
	public GvWindowSystem getInstance(int width, 
			int height, 
			String windowTitle,
			GvEventListener eventListener
			) {
		
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		
		lCanvas = new GvCanvasAWT( glcapabilities );
		lCanvas.setEventListener(eventListener);
		
		lWindow = new GvWindowAWT( windowTitle );
		lWindow.setCanvas( lCanvas );
		final GvWindowAWT lWindowAWT = (GvWindowAWT)lWindow;
		lWindowAWT.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				lWindowAWT.remove( (GLCanvas)lCanvas );
				lWindowAWT.dispose();
				System.exit( 0 );
			}
		});

		lWindowAWT.setSize( width, height );
		lWindowAWT.setVisible( true );
		
		return this;
	}

}

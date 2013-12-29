package de.grovie.engine.renderer.windowsystem.AWT;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.engine.renderer.GvRenderer;
import de.grovie.engine.renderer.windowsystem.GvWindowSystem;

public class GvWindowSystemAWT extends GvWindowSystem {
	
	public GvWindowSystem getInstance(GvRenderer renderer) {
		
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );
		
		lIOListener = new GvIOListenerAWT();		//create listener for keyboard/mouse events
		
		lCanvas = new GvCanvasAWT( glcapabilities );
		lCanvas.setEventListener(renderer.getEventListener());	//set opengl callback listener
		lCanvas.setIOListener(lIOListener);			//set listener for keyboard/mouse events
		
		lIOListener.setRenderer(renderer);				//give io listener reference to canvas (for redrawing)
		
		lWindow = new GvWindowAWT( renderer.getWindowTitle() );
		lWindow.setCanvas( lCanvas );
		final GvWindowAWT lWindowAWT = (GvWindowAWT)lWindow;
		lWindowAWT.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				lWindowAWT.remove( (GLCanvas)lCanvas );
				lWindowAWT.dispose();
				System.exit( 0 );
			}
		});

		lWindowAWT.setSize( renderer.getRendererStateMachine().getScreenWidth(),
				renderer.getRendererStateMachine().getScreenHeight());
		lWindowAWT.setVisible( true );
		
		return this;
	}

}

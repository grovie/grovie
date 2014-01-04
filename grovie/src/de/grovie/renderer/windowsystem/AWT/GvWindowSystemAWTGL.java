package de.grovie.renderer.windowsystem.AWT;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

public class GvWindowSystemAWTGL extends GvWindowSystem {

	@Override
	public GvWindowSystem getInstance(GvRenderer renderer) 
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );

		lIOListener = new GvIOListenerAWT();		//create listener for keyboard/mouse events

		lCanvas = new GvCanvasAWTGL( glcapabilities );
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

	@Override
	public GvWindowSystem getInstanceInvisible(Object sharedContext) 
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );

		//create invisible canvas
		lCanvas = new GvCanvasAWTGL( glcapabilities );
		
		return this;
	}
}

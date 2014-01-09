package de.grovie.renderer.windowsystem.swing;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.swing.JFrame;

import de.grovie.renderer.GvRenderer;
import de.grovie.renderer.windowsystem.GvWindowSystem;

public class GvWindowSystemSwingGL extends GvWindowSystem {
	
	@Override
	public GvWindowSystemSwingGL getInstance(GvRenderer renderer) 
	{
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities( glprofile );

		lIOListener = new GvIOListenerSwing();		//create listener for keyboard/mouse events

		lCanvas = new GvCanvasSwingGL( glcapabilities );
		lCanvas.setIOListener(lIOListener);			//set listener for keyboard/mouse events

		lIOListener.setRenderer(renderer);			//give io listener reference to canvas (for redrawing)

		lWindow = new GvWindowSwing( renderer.getWindowTitle() );
		
		final JFrame lWindowSwing = (JFrame)lWindow;
		lWindowSwing.addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent windowevent ) {
				lWindowSwing.dispose();
				System.exit( 0 );
			}
		});

		lWindow.setCanvas( lCanvas );
		lWindowSwing.setSize( renderer.getRendererStateMachine().getScreenWidth(),
				renderer.getRendererStateMachine().getScreenHeight());
		lWindowSwing.setVisible( true );

		return this;
	}

	@Override
	public GvWindowSystem getInstanceInvisible(Object sharedContext) {
		// TODO Auto-generated method stub
		return null;
	}
}

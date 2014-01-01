package de.grovie.renderer.GL2;

import de.grovie.renderer.GvBufferSet;
import de.grovie.renderer.GvContext;
import de.grovie.renderer.GvDevice;

public class GvBufferSetGL2 extends GvBufferSet {

	public GvBufferSetGL2(GvDevice device, GvContext context) {
		super(device, context);
	}

	@Override
	protected void insertIntoArrayBuffers(float[] vertices, float[] normals,
			float[] uvcoords) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void insertIntoElementBuffer(int[] indices) {
		// TODO Auto-generated method stub
		
	}

}

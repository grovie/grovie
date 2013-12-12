package de.grovie.engine;

import de.grovie.data.GvData;

public abstract class GvEventListener {

	private GvData lData;
	
	public void reshape(Object gl, int x, int y, int width, int height)
	{
		
	}
	
	public void init(Object gl)
	{
		
	}
	
	public void display(Object gl)
	{
		
	}
	
	public void dispose(Object gl)
	{
		
	}
	
	public void setData(GvData data)
	{
		lData = data;
	}
	
	public GvData getData()
	{
		return lData;
	}
}

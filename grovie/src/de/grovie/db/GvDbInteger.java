package de.grovie.db;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsgDataSceneUpdate;

public class GvDbInteger implements GvMsgDataSceneUpdate{

	private int lInt;
	
	public GvDbInteger()
	{
		lInt=0;
	}
	
	public int getInt()
	{
		return lInt;
	}
	
	public void increment()
	{
		lInt++;
	}

	@Override
	public void process(GvData target) {
		target.receiveSceneUpdate(this);
	}
}

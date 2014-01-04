package de.grovie.engine.concurrent;

import de.grovie.data.GvData;

public interface GvMsgData extends GvMsg<GvData> {

	@Override
	public void process(GvData target);
	
}

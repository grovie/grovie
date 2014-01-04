package de.grovie.data.message;

import de.grovie.data.GvData;
import de.grovie.engine.concurrent.GvMsg;

public interface GvMsgData extends GvMsg<GvData> {

	@Override
	public void process(GvData target);
	
}

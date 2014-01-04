package de.grovie.data.message;

import de.grovie.data.GvData;

public class GvMsgDataNewContext implements GvMsgData {

	Object lSharedContext;
	
	public GvMsgDataNewContext(Object sharedContext)
	{
		lSharedContext = sharedContext;
	}
	
	@Override
	public void process(GvData target) {
		target.setupContext(lSharedContext);
	}
}

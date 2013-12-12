package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMessage;

public class TestMessageDone implements GvMessage {

	public String lMsg;
	
	public TestMessageDone(String msg)
	{
		lMsg = msg;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
}

package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMsg;

public class TestMessageDone implements GvMsg {

	public String lMsg;
	
	public TestMessageDone(String msg)
	{
		lMsg = msg;
	}

	@Override
	public void process(Object target) {
		// TODO Auto-generated method stub
		
	}


	
}

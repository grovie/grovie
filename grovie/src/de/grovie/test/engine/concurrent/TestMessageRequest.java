package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMessage;
import de.grovie.engine.concurrent.GvMessageQueue;

public class TestMessageRequest implements GvMessage{

	public double lNum ;
	
	public TestMessageRequest(double num)
	{
		lNum = num;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}

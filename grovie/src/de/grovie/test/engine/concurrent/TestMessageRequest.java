package de.grovie.test.engine.concurrent;

import de.grovie.engine.concurrent.GvMsg;

public class TestMessageRequest implements GvMsg{

	public double lNum ;
	
	public TestMessageRequest(double num)
	{
		lNum = num;
	}

	@Override
	public void process(Object target) {
		// TODO Auto-generated method stub
		
	}
	


}

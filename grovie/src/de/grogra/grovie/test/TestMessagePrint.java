package de.grogra.grovie.test;

import de.grovie.engine.concurrent.GvMessage;

public class TestMessagePrint implements GvMessage{

	private Object lMessage;
	
	public TestMessagePrint(Object message)
	{
		lMessage = message;
	}
	
	@Override
	public void run() {
		System.out.println(lMessage);
	}
}

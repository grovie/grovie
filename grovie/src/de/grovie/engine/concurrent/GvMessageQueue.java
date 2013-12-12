package de.grovie.engine.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

public class GvMessageQueue {

	private LinkedBlockingQueue<GvMessage> lQueue;
	
	public GvMessageQueue()
	{
		lQueue = new LinkedBlockingQueue<GvMessage>();
	}
	
	public synchronized void post(GvMessage message)
	{
		lQueue.add(message);
	}
	
	
}

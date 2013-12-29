package de.grovie.engine.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

public class GvMessageQueue {

	private LinkedBlockingQueue<GvMessage> lQueue;
	
	public GvMessageQueue()
	{
		lQueue = new LinkedBlockingQueue<GvMessage>();
	}
	
	public synchronized void put(GvMessage message) throws InterruptedException
	{
		lQueue.put(message);
	}
	
	public synchronized GvMessage take() throws InterruptedException
	{
		return lQueue.take();
	}
	
	public synchronized GvMessage poll()
	{
		return lQueue.poll();
	}

	public void offer(GvMessage message) {
		lQueue.offer(message);
	}
	
	public int size()
	{
		return lQueue.size();
	}
}

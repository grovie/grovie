package de.grovie.engine.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

public class GvMsgQueue<E> {

	private LinkedBlockingQueue<GvMsg<E> > lQueue;
	
	public GvMsgQueue()
	{
		lQueue = new LinkedBlockingQueue<GvMsg<E> >();
	}
	
//	public synchronized void put(GvMessage message) throws InterruptedException
//	{
//		lQueue.put(message);
//	}
//	
//	public synchronized GvMessage take() throws InterruptedException
//	{
//		return lQueue.take();
//	}
	
	public synchronized GvMsg<E> poll()
	{
		return lQueue.poll();
	}

	public synchronized void offer(GvMsg<E> message) {
		lQueue.offer(message);
	}
	
	public synchronized int size()
	{
		return lQueue.size();
	}
}

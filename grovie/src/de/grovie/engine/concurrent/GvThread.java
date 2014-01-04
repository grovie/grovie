package de.grovie.engine.concurrent;

import de.grovie.exception.GvExEngineConcurrentThreadInitFail;

public abstract class GvThread implements Runnable{
	
	public GvThread()
	{
	}

	@Override
	public void run() {
		try {
			runThread();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (GvExEngineConcurrentThreadInitFail e) {
			e.printStackTrace();
		}
	}
	
	public abstract void runThread() throws InterruptedException, GvExEngineConcurrentThreadInitFail;
}

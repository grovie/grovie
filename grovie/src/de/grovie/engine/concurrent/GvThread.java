package de.grovie.engine.concurrent;

public abstract class GvThread implements Runnable{
	
	public GvThread()
	{
	}

	@Override
	public void run() {
		try {
			runThread();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public abstract void runThread() throws InterruptedException;
}

package de.grogra.grovie.test;

import de.grovie.engine.concurrent.GvService;

public class TestService {
	public static void main(String[] args) {
		GvService svc = new GvService(5);
		
		svc.post(new TestMessagePrint((Object)2.0));
		svc.post(new TestMessagePrint((Object)4.0));
		svc.post(new TestMessagePrint((Object)9.0));
		
		svc.shutdownAndAwaitTermination();
		
	}
	
	

	
}

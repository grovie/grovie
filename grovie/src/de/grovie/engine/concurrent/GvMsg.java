package de.grovie.engine.concurrent;

public interface GvMsg<E> {
	public void process(E target);
}

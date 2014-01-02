package de.grovie.renderer.renderstate;

public interface GvRenderStateItem<E> {

	boolean isDifferent(E item);
	void set(E item);
}

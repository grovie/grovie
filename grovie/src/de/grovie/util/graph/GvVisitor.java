package de.grovie.util.graph;

import com.tinkerpop.blueprints.Vertex;

public abstract class GvVisitor {

	public abstract void visit(Vertex vertex);
	
	public abstract void leave(Vertex vertex);
	
}

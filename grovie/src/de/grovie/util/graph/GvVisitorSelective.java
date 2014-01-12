package de.grovie.util.graph;

import com.tinkerpop.blueprints.Vertex;

/**
 * selective visitor tells traversal to stop proceeding at selective parts of graph
 * @author yong
 *
 */
public abstract class GvVisitorSelective {

	public abstract boolean visit(Vertex vertex); 
	
	public abstract void leave(Vertex vertex);
}

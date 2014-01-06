package de.grovie.util.graph;

import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

//FOR DEBUG
public class GvVisitorCopy extends GvVisitor {

	private Object lVertexCopyId;
	private TransactionalGraph lGraph;
	
	public GvVisitorCopy(Object copy, TransactionalGraph graph)
	{
		lVertexCopyId = copy;
		lGraph = graph;
	}
	
	public void setVertexCopy(Object copy)
	{
		lVertexCopyId = copy;
	}
	
	public TransactionalGraph getGraph()
	{
		return lGraph;
	}
	
	public Vertex getVertexCopy()
	{
		return lGraph.getVertex(lVertexCopyId);
	}
	
	@Override
	public void visit(Vertex vertex) {
		Vertex newVertex = lGraph.getVertex(lVertexCopyId);
		
		newVertex.setProperty("Type",vertex.getProperty("Type"));
		
		if(vertex.getProperty("Type").equals("Tube"))
		{
			//get length and radius
			float length = ((Float)vertex.getProperty("Length")).floatValue();
			float radius = ((Float)vertex.getProperty("Radius")).floatValue();

			newVertex.setProperty("Length", new Float(length));
			newVertex.setProperty("Radius", new Float(radius));			
		}
	}

}
//END DEBUG
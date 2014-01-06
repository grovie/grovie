package de.grovie.util.graph;

import java.util.Iterator;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.grovie.exception.GvExDbSceneDuplicated;

//FOR DEBUG
public class GvGraphUtil {

	public static Vertex getVertexScene(TransactionalGraph graph) throws GvExDbSceneDuplicated
	{
		Iterable<Vertex> vertexSceneList = graph.getVertices("Type", "Scene");
		Iterator<Vertex> vertexSceneIterator = vertexSceneList.iterator();
		if(vertexSceneIterator.hasNext())
		{
			Vertex scene = vertexSceneIterator.next();

			if(vertexSceneIterator.hasNext())
			{
				throw new GvExDbSceneDuplicated("More than one scene in database");
			}

			return scene;
		}

		return null;
	}

	public static Vertex getVertexStep(Vertex scene, int stepId)
	{
		Iterable<Vertex> stepVertices = GvGraphUtil.getVerticesRefine(scene);
		Iterator<Vertex> stepVertexIter = stepVertices.iterator();

		Vertex stepVertex = null;
		while(stepVertexIter.hasNext())
		{
			stepVertex = stepVertexIter.next();
			Integer currStepId = stepVertex.getProperty("Step");
			if(currStepId.intValue() == stepId)
				return stepVertex;
		}
		return null;
	}

	public static void clear(TransactionalGraph graph)
	{
		Iterable<Vertex> vertices = graph.getVertices();
		Iterable<Edge> edges = graph.getEdges();

		Iterator<Vertex> vertexIter = vertices.iterator();
		Iterator<Edge> edgeIter = edges.iterator();

		while(edgeIter.hasNext())
		{
			graph.removeEdge(edgeIter.next());
		}

		while(vertexIter.hasNext())
		{
			graph.removeVertex(vertexIter.next());
		}

		graph.commit();
	}

	public static Iterable<Vertex> getVerticesRefine(Vertex vertex)
	{
		return vertex.getVertices(Direction.OUT, "Refinement");
	}
	
	public static Iterable<Vertex> getVerticesBranch(Vertex vertex)
	{
		return vertex.getVertices(Direction.OUT, "Branch");
	}

	public static Iterable<Vertex> getVertices(Vertex vertex, String edgeLabel)
	{
		return vertex.getVertices(Direction.OUT, edgeLabel);
	}
	
	public static Iterable<Edge> getEdges(Vertex vertex, String edgeLabel)
	{
		return vertex.getEdges(Direction.OUT, edgeLabel);
	}
	

	public static void traverseDepthFirst(Vertex vertex, String edgeLabel, GvVisitor visitor)
	{
		visitor.visit(vertex);

		Iterable<Vertex> verticesIterable = GvGraphUtil.getVertices(vertex, edgeLabel);
		Iterator<Vertex> verticesIter = verticesIterable.iterator();

		Vertex vertexCurr = null;
		while(verticesIter.hasNext())
		{
			vertexCurr = verticesIter.next();
			traverseDepthFirst(vertexCurr, edgeLabel, visitor);
		}
	}
	
	private static void copyDepthFirst(long oldVertexId, GvVisitorCopy visitor)
	{
		Vertex oldVertex = visitor.getGraph().getVertex(oldVertexId);
		
		visitor.visit(oldVertex);

		Iterable<Edge> edgesIterable = oldVertex.getEdges(Direction.OUT,"Refinement");
		Iterator<Edge> edgesIter = edgesIterable.iterator();
		
		while(edgesIter.hasNext())
		{
			Edge edgeCurr = edgesIter.next();
			Vertex vertexCurr = edgeCurr.getVertex(Direction.IN);
			
			Vertex vertexNew = visitor.getGraph().addVertex(null);
			visitor.getGraph().addEdge(null, visitor.getVertexCopy(), vertexNew, edgeCurr.getLabel());
			
			visitor.setVertexCopy(vertexNew.getId());
			
			copyDepthFirst((long)vertexCurr.getId(), visitor);
		}
		
		edgesIterable = oldVertex.getEdges(Direction.OUT,"Branch");
		edgesIter = edgesIterable.iterator();
		
		while(edgesIter.hasNext())
		{
			Edge edgeCurr = edgesIter.next();
			Vertex vertexCurr = edgeCurr.getVertex(Direction.IN);
			
			Vertex vertexNew = visitor.getGraph().addVertex(null);
			visitor.getGraph().addEdge(null, visitor.getVertexCopy(), vertexNew, edgeCurr.getLabel());
			
			visitor.setVertexCopy(vertexNew.getId());
			
			copyDepthFirst((long)vertexCurr.getId(), visitor);
		}
		
		edgesIterable = oldVertex.getEdges(Direction.OUT,"Successor");
		edgesIter = edgesIterable.iterator();
		
		while(edgesIter.hasNext())
		{
			Edge edgeCurr = edgesIter.next();
			Vertex vertexCurr = edgeCurr.getVertex(Direction.IN);
			
			Vertex vertexNew = visitor.getGraph().addVertex(null);
			visitor.getGraph().addEdge(null, visitor.getVertexCopy(), vertexNew, edgeCurr.getLabel());
			
			visitor.setVertexCopy(vertexNew.getId());
			
			copyDepthFirst((long)vertexCurr.getId(), visitor);
		}
	}
	
	public static void copyToNextStep(Vertex stepVertex, Vertex sceneVertex, TransactionalGraph graph)
	{
		int oldStepId = ((Integer)stepVertex.getProperty("Step")).intValue();
		
		//create and connect new step vertex
		Vertex stepVertexNew;
		stepVertexNew = graph.addVertex(null);
		stepVertexNew.setProperty( "Step", new Integer(oldStepId+1) );
		graph.addEdge(null, sceneVertex, stepVertexNew, "Refinement");
		
		//create copying visitor
		GvVisitorCopy visitorCopy = new GvVisitorCopy(stepVertexNew.getId(), graph);
		
		//copy step
		copyDepthFirst((long)stepVertex.getId(), visitorCopy);
		
		//commit copy of step
		graph.commit();
	}
}
//END DEBUG
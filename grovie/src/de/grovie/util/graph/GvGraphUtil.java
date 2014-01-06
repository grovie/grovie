package de.grovie.util.graph;

import java.util.Iterator;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.grovie.exception.GvExDbSceneDuplicated;

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

	public static Iterable<Vertex> getVertices(Vertex vertex, String edgeLabel)
	{
		return vertex.getVertices(Direction.OUT, edgeLabel);
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
}

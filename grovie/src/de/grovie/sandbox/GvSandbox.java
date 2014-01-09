package de.grovie.sandbox;

import java.util.Iterator;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.grovie.engine.GvEngine;
import de.grovie.engine.GvEngine.GvEngineMode;
import de.grovie.exception.GvExDbSceneDuplicated;
import de.grovie.exception.GvExDbUnrecognizedImpl;
import de.grovie.util.graph.GvGraphUtil;

public class GvSandbox {

	public static final int kWindowWidth = 640;
	public static final int kWindowHeight = 480;

	public static void main(String[] args)
	{
		//get absolute path to database location

		//create GroViE vis. engine
		GvEngine engine = GvEngine.getInstance(
				GvEngine.GvWindowSystemLibrary.AWT_OPEN_GL,
				GvEngine.GvGraphicsAPI.OPEN_GL_2_0,
				kWindowWidth,
				kWindowHeight,
				"GroViE Sandbox"); //uses embedded db by default

		//start the visualization engine
		if(engine.getMode() == GvEngineMode.EMBEDDED)
		{
			try {
				engine.start("C:\\Users\\yong\\db");
				//engine.start("/Users/yongzhiong/Desktop/testdb");
			} catch (GvExDbUnrecognizedImpl e) {

				e.printStackTrace();
			} //TODO: replace with path obtained from pop up dialog
			catch (GvExDbSceneDuplicated e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//FOR DEBUG
		TransactionalGraph graph = engine.getGraph();
		
		//clear graph
		GvGraphUtil.clear(graph);

		//add test graph
		Vertex firstNode;
		Vertex secondNode;
		Vertex thirdNode;
		Edge edge;
		Edge edge2;
		
		firstNode = graph.addVertex(null);
		firstNode.setProperty( "Type", "Scene" );
		
		secondNode = graph.addVertex(null);
		secondNode.setProperty( "Type", "Step" );
		secondNode.setProperty( "Step", new String("0") );
		
		thirdNode = graph.addVertex(null);
		thirdNode.setProperty( "Type", "Tube" );
		thirdNode.setProperty( "Length", new Float(1.0f) );
		thirdNode.setProperty( "Radius", new Float(0.5f) );

		edge = graph.addEdge(null, firstNode, secondNode, "Refinement");
		edge2 = graph.addEdge(null, secondNode, thirdNode, "Refinement");

		

		for(int i=1; i< 1000; ++i)
		{
			
			try {
				//get scene vertex
				Vertex sceneV = GvGraphUtil.getVertexScene(graph);
				//get previous step vertex
				Vertex stepV = GvGraphUtil.getVertexStep(sceneV, new Integer(i-1).toString());
				//copy step to next step
				GvGraphUtil.copyToNextStep(stepV,sceneV,graph);
				//perform fake rules
				Vertex stepVNew = GvGraphUtil.getVertexStep(sceneV, new Integer(i).toString());
				testRules(stepVNew, graph);
				
				graph.commit();
				
			} catch (GvExDbSceneDuplicated e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			engine.simulationStep(new Integer(i).toString());
			try {
				//Thread.sleep(16); //approx 60 fps
				Thread.sleep(500); 
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//END DEBUG
		
		//begin scene modifications on db (changes should be seen on the rendering window)

		//prevent sandbox application from closing until <Return> key is pressed
		/*try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	public static void testRules(Vertex stepVertex, TransactionalGraph graph)
	{
		elongate(((Long)stepVertex.getId()).longValue(), graph);
	}
	
	private static void elongate(long oldVertexId, TransactionalGraph graph)
	{
		Vertex oldVertex = graph.getVertex(oldVertexId);

		boolean noChild = true;
		
		Iterable<Vertex> vIterable = oldVertex.getVertices(Direction.OUT,"Refinement");
		Iterator<Vertex> vIter = vIterable.iterator();
		while(vIter.hasNext())
		{
			noChild = false;
			Vertex vertexCurr = vIter.next();
			elongate(((Long)vertexCurr.getId()).longValue(), graph);
		}
		
		vIterable = oldVertex.getVertices(Direction.OUT,"Branch");
		vIter = vIterable.iterator();
		while(vIter.hasNext())
		{
			noChild = false;
			Vertex vertexCurr = vIter.next();
			elongate(((Long)vertexCurr.getId()).longValue(), graph);
		}
		
		vIterable = oldVertex.getVertices(Direction.OUT,"Successor");
		vIter = vIterable.iterator();
		while(vIter.hasNext())
		{
			noChild = false;
			Vertex vertexCurr = vIter.next();
			elongate(((Long)vertexCurr.getId()).longValue(), graph);
		}
		
		if(noChild == true)
		{
			Vertex newInternode = graph.addVertex(null);
			newInternode.setProperty("Type",oldVertex.getProperty("Type"));
			newInternode.setProperty("Length",oldVertex.getProperty("Length"));
			newInternode.setProperty("Radius",oldVertex.getProperty("Radius"));
			graph.addEdge(null, oldVertex, newInternode, "Branch");
		}
	}
}

package de.grovie.sandbox;

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
		secondNode.setProperty( "Step", new Integer(0) );
		
		thirdNode = graph.addVertex(null);
		thirdNode.setProperty( "Type", "Tube" );
		thirdNode.setProperty( "Length", new Float(13.0f) );
		thirdNode.setProperty( "Radius", new Float(0.5f) );

		edge = graph.addEdge(null, firstNode, secondNode, "Refinement");
		edge2 = graph.addEdge(null, secondNode, thirdNode, "Refinement");

		graph.commit();

		for(int i=1; i< 1000; ++i)
		{
			
			
			engine.simulationStep(i);
			try {
				Thread.sleep(1000);
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
}

/**
 * 
 */
package de.grogra.grovie.test;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.TransactionalGraph;
import com.tinkerpop.blueprints.Vertex;

import de.grogra.grovie.test.TestDB.RelTypes;
import de.grovie.db.GvDb;
import de.grovie.exception.GvExceptionDbUnrecognizedImpl;



/**
 * @author yong
 *
 */
public class TestArchitect2 {

	//public static GraphDatabaseService graphDb;
	public static GvDb graphDb;
	public static String DB_PATH = "C:\\Users\\yong\\db";
	static Graph graph;
	static TransactionalGraph graphTrans;

	/**
	 * @param args
	 * @throws GvExceptionDbUnrecognizedImpl 
	 */
	public static void main(String[] args) throws GvExceptionDbUnrecognizedImpl {

		System.out.println("TestArchitect Main");

		System.out.println("Test Neo4j");
		createDB();
		testDB();
		System.out.println("Test Neo4j End");

		System.out.println("Test db access- 1. same thread - begin");
		testAccess1();
		System.out.println("Test db access- 1. same thread - end");

		System.out.println("Test JOGL");
		//testGLAWT();
		
		System.out.println("Test JOGL End");

		while(true)
		{

		}
	}
	
	
	public TestArchitect2() throws GvExceptionDbUnrecognizedImpl
	{
		System.out.println("TestArchitect Main");

		System.out.println("Test Neo4j");
		createDB();
		testDB();
		System.out.println("Test Neo4j End");

		System.out.println("Test db access- 1. same thread - begin");
		testAccess1();
		System.out.println("Test db access- 1. same thread - end");

		System.out.println("Test JOGL");
		testGLAWT();
		System.out.println("Test JOGL End");

		while(true)
		{

		}
	}

	private static void createDB() throws GvExceptionDbUnrecognizedImpl
	{
		graphDb = GvDb.getInstance(DB_PATH);
		graph = graphDb.getGraph();
		graphTrans = (TransactionalGraph)graph;
	}

	private static void registerShutdownHook( final GraphDatabaseService graphDb )
	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}

	private static void testDB()
	{
		try{
			new TestDB2(graphDb);
		}catch(Exception e)
		{
			System.out.println("testDB failed.");
		}
	}
	
	private static void testGLAWT()
	{
		try{
			GLProfile glprofile = GLProfile.getDefault();
			GLCapabilities glcapabilities = new GLCapabilities( glprofile );
			final GLCanvas glcanvas = new GLCanvas( glcapabilities );

			glcanvas.addGLEventListener( new GLEventListener() {

				@Override
				public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
					OneTriangleAWT.setup( glautodrawable.getGL().getGL2(), width, height );
				}

				@Override
				public void init( GLAutoDrawable glautodrawable ) {
				}

				@Override
				public void dispose( GLAutoDrawable glautodrawable ) {
				}

				@Override
				public void display( GLAutoDrawable glautodrawable ) {
					OneTriangleAWT.render( glautodrawable.getGL().getGL2(), glautodrawable.getWidth(), glautodrawable.getHeight() );
				}
			});

			final Frame frame = new Frame( "One Triangle AWT" );
			frame.add( glcanvas );
			frame.addWindowListener( new WindowAdapter() {
				public void windowClosing( WindowEvent windowevent ) {
					frame.remove( glcanvas );
					frame.dispose();
					System.exit( 0 );
				}
			});

			frame.setSize( 640, 480 );
			frame.setVisible( true );
		}
		catch(Exception e)
		{
			System.out.println("testGL failed.");
		}
	}

	/**
	 * Test database access from same thread
	 */
	private static void testAccess1()
	{
		for(int i=0; i<100; ++i)
		{
			testAccess1Internal();
		}
	}

	private static synchronized void testAccess1Internal()
	{
		Vertex firstNode;
		Vertex secondNode;
		Edge e;
		
		//create nodes and print data
		//Transaction tx = graphDb.beginTx();

			firstNode = graph.addVertex(null);
			firstNode.setProperty( "message", "Hello 1, " );
			secondNode = graph.addVertex(null);
			secondNode.setProperty( "message", "World!" );

			e = graph.addEdge(null, firstNode, secondNode, "knows");
			e.setProperty( "message", "brave Neo4j " );

			graphTrans.commit();

			System.out.print( firstNode.getProperty( "message" ) );
			System.out.print( e.getProperty( "message" ) );
			System.out.print( secondNode.getProperty( "message" ) );
			System.out.println();
	
	
			graph.removeEdge(e);
			graph.removeVertex(firstNode);
			graph.removeVertex(secondNode);

			graphTrans.commit();
		
	}
}

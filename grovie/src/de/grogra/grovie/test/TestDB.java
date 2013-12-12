package de.grogra.grovie.test;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class TestDB implements Runnable{

	Thread t;
	
	GraphDatabaseService graphDb;

	/*private final Object lock = new Object();
	private volatile boolean suspend = false , stopped = false;*/

	enum RelTypes implements RelationshipType
	{
		CONTAINED_IN, KNOWS
	}

	public TestDB(GraphDatabaseService db)
	{
		graphDb = db;

		// Create a new, second thread
		t = new Thread(this, "NeoTest DB Thread");
		System.out.println("NeoTest DB thread: " + t);
		t.start(); // Start the thread
	}

	@Override
	public void run() {

		//test neo4j access - 2. other thread
		System.out.println("Test db access- 2.other thread - begin");
		testAccess2();
		System.out.println("Test db access- 2.other thread - end");

		/*
		while(!stopped){
			while (!suspend){
				// do work
			}
			synchronized (lock){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}*/
	}

	/*
	public void suspend(){
		suspend = true;
	}
	public void stop(){
		suspend = true;stopped = true;
		synchronized (lock){
			lock.notifyAll();
		}
	}

	public void resume(){
		suspend = false;
		synchronized (lock){
			lock.notifyAll();
		}
	}
	*/

	private void testAccess2()
	{
		for(int i=0; i<100; ++i)
		{
			testAccess2Internal();
		}
	}

	private synchronized void testAccess2Internal()
	{
		Node firstNode;
		Node secondNode;
		Relationship relationship;
		
		//create nodes and print data
		Transaction tx = graphDb.beginTx();
		try
		{

			firstNode = graphDb.createNode();
			firstNode.setProperty( "message", "Hello 2, " );
			secondNode = graphDb.createNode();
			secondNode.setProperty( "message", "World!" );

			relationship = firstNode.createRelationshipTo( secondNode, RelTypes.KNOWS );
			relationship.setProperty( "message", "brave Neo4j " );

			tx.success();

			System.out.print( firstNode.getProperty( "message" ) );
			System.out.print( relationship.getProperty( "message" ) );
			System.out.print( secondNode.getProperty( "message" ) );
			System.out.println();
		}
		finally
		{
			tx.finish();
		}

		//delete nodes
		tx = graphDb.beginTx();
		try
		{

			firstNode.getSingleRelationship( RelTypes.KNOWS, Direction.OUTGOING ).delete();
			firstNode.delete();
			secondNode.delete();

			tx.success();
		}
		finally
		{
			tx.finish();
		}
	}

	/*
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
	}*/
}

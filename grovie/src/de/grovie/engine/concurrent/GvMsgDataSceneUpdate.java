package de.grovie.engine.concurrent;

import com.tinkerpop.blueprints.TransactionalGraph;

import de.grovie.data.GvData;

public class GvMsgDataSceneUpdate implements GvMsgData {

	private String lStepId;
	private TransactionalGraph lGraph;
	
	public GvMsgDataSceneUpdate(String stepId, TransactionalGraph graph)
	{
		lStepId = stepId;
		lGraph = graph;
	}
	
	@Override
	public void process(GvData target) {
		target.receiveSceneUpdate(lStepId, lGraph);
	}

}

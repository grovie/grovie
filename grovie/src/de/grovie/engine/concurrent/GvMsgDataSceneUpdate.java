package de.grovie.engine.concurrent;

import com.tinkerpop.blueprints.Graph;

import de.grovie.data.GvData;

public class GvMsgDataSceneUpdate implements GvMsgData {

	private int lStepId;
	private Graph lGraph;
	
	public GvMsgDataSceneUpdate(int stepId, Graph graph)
	{
		lStepId = stepId;
		lGraph = graph;
	}
	
	@Override
	public void process(GvData target) {
		target.receiveSceneUpdate(lStepId, lGraph);
	}

}

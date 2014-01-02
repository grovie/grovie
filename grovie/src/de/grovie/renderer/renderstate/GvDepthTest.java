package de.grovie.renderer.renderstate;

public class GvDepthTest implements GvRenderStateItem<GvDepthTest>{

	public enum GvDepthTestFunction
	{
		NEVER,
		LESS,
		EQUAL,
		LESS_THAN_OR_EQUAL,
		GREATER,
		NOT_EQUAL,
		GREATER_THAN_OR_EQUAL,
		ALWAYS
	}
	
	public boolean lEnabled;
	public GvDepthTestFunction lFunction;
	
	public GvDepthTest()
	{
		lEnabled=false;
		lFunction = GvDepthTestFunction.LESS_THAN_OR_EQUAL;
	}

	@Override
	public boolean isDifferent(GvDepthTest item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		if(this.lFunction != item.lFunction)
			return true;
		
		return false;
	}

	@Override
	public void set(GvDepthTest item) {
		this.lEnabled = item.lEnabled;
		this.lFunction = item.lFunction;
	}
}

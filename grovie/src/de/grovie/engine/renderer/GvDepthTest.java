package de.grovie.engine.renderer;

public class GvDepthTest {

	public enum GvDepthTestFunction
	{
		Never,
		Less,
		Equal,
		LessThanOrEqual,
		Greater,
		NotEqual,
		GreaterThanOrEqual,
		Always
	}
	
	public boolean lEnabled;
	public GvDepthTestFunction lFunction;
	
	public GvDepthTest()
	{
		lEnabled=true;
		lFunction = GvDepthTestFunction.Less;
	}
}

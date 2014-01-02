package de.grovie.renderer.renderstate;

public class GvStencilTest implements GvRenderStateItem<GvStencilTest>{

	public boolean lEnabled;
	
	public GvStencilTest()
	{
		lEnabled = false;
	}
	
	@Override
	public boolean isDifferent(GvStencilTest item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		
		return false;
	}

	@Override
	public void set(GvStencilTest item) {
		this.lEnabled = item.lEnabled;
	}
}

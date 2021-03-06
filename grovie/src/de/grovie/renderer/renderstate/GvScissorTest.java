package de.grovie.renderer.renderstate;

public class GvScissorTest  implements GvRenderStateItem<GvScissorTest>{

	public boolean lEnabled;
	
	public GvScissorTest()
	{
		lEnabled = false;
	}
	
	@Override
	public boolean isDifferent(GvScissorTest item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		
		return false;
	}

	@Override
	public void set(GvScissorTest item) {
		this.lEnabled = item.lEnabled;
	}
}

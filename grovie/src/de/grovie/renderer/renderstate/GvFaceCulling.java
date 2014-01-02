package de.grovie.renderer.renderstate;

public class GvFaceCulling implements GvRenderStateItem<GvFaceCulling>{

	public boolean lEnabled;
	
	public GvFaceCulling()
	{
		lEnabled = false;
	}
	
	@Override
	public boolean isDifferent(GvFaceCulling item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		
		return false;
	}

	@Override
	public void set(GvFaceCulling item) {
		this.lEnabled = item.lEnabled;
	}

}

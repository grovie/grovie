package de.grovie.renderer.renderstate;

public class GvLighting implements GvRenderStateItem<GvLighting>{

	public boolean lEnabled;
	
	public GvLighting()
	{
		lEnabled = false;
	}
	
	@Override
	public boolean isDifferent(GvLighting item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		
		return false;
	}

	@Override
	public void set(GvLighting item) {
		this.lEnabled = item.lEnabled;
	}
}

package de.grovie.renderer.renderstate;

public class GvTexture implements GvRenderStateItem<GvTexture>{

	public boolean lEnabled;
	
	public GvTexture()
	{
		lEnabled = false;
	}
	
	@Override
	public boolean isDifferent(GvTexture item) {
		if(this.lEnabled != item.lEnabled)
			return true;
		
		return false;
	}

	@Override
	public void set(GvTexture item) {
		this.lEnabled = item.lEnabled;
	}

}

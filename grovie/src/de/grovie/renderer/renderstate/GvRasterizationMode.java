package de.grovie.renderer.renderstate;

public class GvRasterizationMode implements GvRenderStateItem<GvRasterizationMode>{

	public enum GvRasterFace
	{
		FRONT,
		BACK,
		FRONT_AND_BACK
	}
	
	public enum GvRasterMode
	{
		POINT,
		LINE,
		FILL
	};
	
	public GvRasterFace lFace;
	public GvRasterMode lMode;
	
	public GvRasterizationMode()
	{
		lFace = GvRasterFace.FRONT_AND_BACK;
		lMode = GvRasterMode.FILL;
	}
	
	@Override
	public boolean isDifferent(GvRasterizationMode item) {
		if(lFace != item.lFace)
			return true;
		if(lMode != item.lMode)
			return true;
		
		return false;
	}

	@Override
	public void set(GvRasterizationMode item) {
		this.lFace = item.lFace;
		this.lMode = item.lMode;
	}

}

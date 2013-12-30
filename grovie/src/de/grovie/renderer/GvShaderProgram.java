package de.grovie.renderer;

/**
 * This class represents a shader program, 
 * containing a vertex shader and a fragment shader.
 * 
 * @author yong
 *
 */
public class GvShaderProgram {

	private int lId;					//shader program id
	private String lShaderVertex;		//vertex shader code
	private String lShaderFragment;	//fragment shader code
	
	public GvShaderProgram(int id, String shaderVertex, String shaderFragment)
	{
		this.lId = id;
		this.lShaderVertex = shaderVertex;
		this.lShaderFragment = shaderFragment;
	}
	
	public int getId() {
		return lId;
	}
	public void setId(int id) {
		this.lId = id;
	}
	public String getShaderVertex() {
		return lShaderVertex;
	}
	public void setShaderVertex(String shaderVertex) {
		this.lShaderVertex = shaderVertex;
	}
	public String getShaderFragment() {
		return lShaderFragment;
	}
	public void setShaderFragment(String shaderFragment) {
		this.lShaderFragment = shaderFragment;
	}
}

package de.grovie.renderer.shader.gl2.standard;

public class GvTextureMaterialTriangleF {

	public final static String kSource = 
			"uniform sampler2D texture1;"+"\n"+
			"void main()"+"\n"+
			"{"+"\n"+
			"	gl_FragColor = gl_Color * texture2D(texture1, gl_TexCoord[0].st);"+"\n"+
			"}"
			;
}

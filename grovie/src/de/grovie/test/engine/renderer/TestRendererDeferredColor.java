package de.grovie.test.engine.renderer;

public class TestRendererDeferredColor {

	public static String[] PROGRAM_V ={
		"void main( void )                                            \n"+
				"{                                                            \n"+
				"    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;   \n"+
				//				"    gl_FrontColor = vec4(1.0, 1.0, 1.0, 1.0);                 \n"+
				"}"
	};

	public static String[] PROGRAM_F ={
		"uniform sampler2D tImage0;                                                          \n" +
				"uniform sampler2D tImage1;                                                          \n" +
				"uniform sampler2D tImage2;                                                          \n" +
				"uniform vec2 widthHeight;	                                                         \n" +
				"void main( void )                                                                   \n" +
				"{                                                                                   \n" +
				"   vec2 adjust = vec2(0.5,0.5);						                         	 \n" +
				"	vec2 coor = (gl_FragCoord.xy + adjust) / widthHeight;			 				 \n" +
				"   vec4 diff = texture2D( tImage0, coor );             			            	 \n" +
				"   vec4 spec = texture2D( tImage1, coor );				                           	 \n" +
				"   vec4 color = texture2D( tImage2, coor );				                           	 \n" +
				"                                                                                    \n" +                                                   
				"	gl_FragColor = (color * diff) + (color * spec);							 \n" +
				"}"
	};
}

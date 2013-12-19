package de.grovie.test.engine.renderer;

public class TestRendererDeferredGBuffer {

	public static String[] PROGRAM_V ={
			"varying vec3 normal;\n" +
			"                                                             							\n"+
			"void main( void )                                            							\n"+
			"{                                                            							\n"+
			"    normal = normalize(gl_NormalMatrix * gl_Normal);									\n"+
			"                                                             							\n"+
			"    gl_FrontColor = gl_Color;                 											\n"+
			"    gl_Position = ftransform();	   													\n"+
			"}"
	};
	
	public static String[] PROGRAM_F ={
			"varying vec3 normal;                              			  \n" +
			"                                                             \n" +
			"void main( void )                                            \n" +
			"{                                                            \n" +
//			"   const float LOG2 = 1.442695;                              \n" +
//			"   float z = gl_FragCoord.z / gl_FragCoord.w / 100.0f;	  \n" +
//			"   float fogFactor = exp2( -gl_Fog.density * 				  \n" +
//			"		   gl_Fog.density * 								  \n" +
//			"		   z * 												  \n" +
//			"		   z * 												  \n" +
//			"		   LOG2 );											  \n" +
//			"   fogFactor = clamp(fogFactor, 0.0, 1.0);					  \n" +
			"   gl_FragData[0] = vec4(normalize(normal),0);               \n" +	//very important to normalize here as normals from vertex shader have been interpolated and not normalized. May introduce edges if not normalized.
			"   gl_FragData[1] = gl_Color;                     			  \n" +
//			"   gl_FragData[1] =  texture2D(color_texture, gl_TexCoord[0].st);\n" +
//					"   gl_FragData[1] = mix(gl_Fog.color, texture2D(color_texture, gl_TexCoord[0].st), fogFactor);\n" +
			"}"
	};
}

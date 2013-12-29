package de.grovie.test.engine.renderer;

public class TestRendererDeferredLight {

	public static String[] PROGRAM_V ={
		"void main( void )                                            \n"+
				"{                                                            \n"+
				"    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;   \n"+
				//				"    gl_FrontColor = vec4(1.0, 1.0, 1.0, 1.0);                 \n"+
				"}"
	};

	public static String[] PROGRAM_F ={
		"uniform sampler2D tImage0;                                                          	\n" +
		"uniform sampler2D tImage1;                                                          	\n" +
		"uniform sampler2D tImage2;                                                          	\n" +
		"uniform sampler2D tImage3;                                                          	\n" +
		"uniform vec3 light;                                                               		\n" +
		"uniform vec4 lightDiff;                                                    			\n" +
		"uniform vec4 lightSpec;                                                    			\n" +
		"uniform vec2 lightPow;                                                    		 		\n" +
		"uniform vec3 cameraPosition;                                                        	\n" +
		"uniform mat4 viewMatrixInv; 															\n" +
		"uniform vec2 clipPlanes; 																\n" +
		"uniform vec2 windowSize; 																\n" +
		"uniform vec2 rightAndTop; 																\n" +
		"uniform int lightType; 																\n" +
		"                                                                                    	\n" +
		"                          																\n"+
		"void main( void )                                                                   	\n" +
		"{                                                                                   	\n" +
		//    ndc - normalized device coordinates
		"    vec3 ndcPos;                                                              			\n"+
		"    vec2 adjust = vec2(0.5,0.5);						                         	 	\n" +
		"	 vec2 coor = (gl_FragCoord.xy+adjust) / windowSize;			 				 		\n" +
		"    ndcPos.xy = coor;                                		 							\n"+
		"    ndcPos.z = texture2D(tImage0, coor).r; // or gl_FragCoord.z      					\n"+
		"    ndcPos.xy -= 0.5;                                                            		\n"+
		"    ndcPos.xy *= 2.0;                                                            		\n"+
		//    restoring world-space position from depth
		" 	 vec4 viewPos; 																		\n"+
		"    viewPos.z = -clipPlanes.x / (clipPlanes.y - (ndcPos.z * (clipPlanes.y-clipPlanes.x))) * clipPlanes.y; 					\n"+
		"    viewPos.x = ndcPos.x * rightAndTop.x;   											\n" +                                                                            
		"    viewPos.y = ndcPos.y * rightAndTop.y;   											\n" +
		"    float scale = -viewPos.z / clipPlanes.x; 													\n"+
		"    viewPos.x *= scale; 																\n"+
		"    viewPos.y *= scale; 																\n"+
		"    viewPos.w = 1.0;                                                                     \n"+
		"    vec3 position = (viewMatrixInv * viewPos).xyz;                                		\n"+ 
		"    vec3 normal = texture2D( tImage1, coor).xyz;                           			\n" +
		"    vec4 prevDiff = texture2D( tImage2, coor );                         				\n" +
		"    vec4 prevSpec = texture2D( tImage3, coor );                         				\n" +
		//    diffuse component   
		"    float NdotL = max(dot(normal,light), 0.0);												\n" +
		"	 gl_FragData[0] = prevDiff + (NdotL*lightDiff); \n" +
		//    specular component
		"    vec3 halfVector = light + normalize(cameraPosition-position);"+
		"    if(NdotL > 0.0)"+
		"    {"+
		"        float NdotHV = max(dot(normal,halfVector),0.0);"+
		"        gl_FragData[1] = prevSpec + lightSpec * pow(NdotHV,0.2) ;"+
		"    }"+
		"    else {"+
		"        gl_FragData[1]= prevSpec;"+
		"    }"+
		"}"
	};
}

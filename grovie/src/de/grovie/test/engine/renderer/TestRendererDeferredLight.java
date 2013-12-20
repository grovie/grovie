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
		//				"subroutine uniform lightDirection lightSelectDir;										\n" +
		"                                                                                    	\n" +
		"#define ZNEAR clipPlanes.x       														\n"+
		"#define ZFAR clipPlanes.y        														\n"+
		"                          																\n"+
		//				"subroutine vec3 lightDirection(vec3 pos);												\n"+
		//				"																						\n"+
		//				"subroutine (lightDirection ) vec3 directedDirection(vec3 pos) {						\n"+
		//				"	return light;																		\n"+
		//				"}																						\n"+
		//				"																						\n"+
		//				"subroutine (lightDirection ) vec3 pointDirection(vec3 pos) {							\n"+
		//				"	return light-pos;																	\n"+
		//				"}																						\n"+
		//				"vec3 directedDirection(vec3 pos) {														\n"+
		//				"	return light;																		\n"+
		//				"}																						\n"+
		//				"																						\n"+
		//				"vec3 pointDirection(vec3 pos) {														\n"+
		//				"	return light-pos;																	\n"+
		//				"}																						\n"+

		"void main( void )                                                                   	\n" +
		"{                                                                                   	\n" +
		"    vec3 ndcPos;                                                              			\n"+
		"    vec2 adjust = vec2(0.5,0.5);						                         	 	\n" +
		"	 vec2 coor = (gl_FragCoord.xy+adjust) / windowSize;			 				 		\n" +
		"    ndcPos.xy = coor;                                		 							\n"+
		"    ndcPos.z = texture2D(tImage0, coor).r; // or gl_FragCoord.z      					\n"+
		"    ndcPos.xy -= 0.5;                                                            		\n"+
		"    ndcPos.xy *= 2.0;                                                            		\n"+
		"                                                                                  		\n" +
		" 	 vec4 viewPos; 																		\n"+
		"    viewPos.z = -ZNEAR / (ZFAR - (ndcPos.z * (ZFAR-ZNEAR))) * ZFAR; 					\n"+
		"    viewPos.x = ndcPos.x * rightAndTop.x;   											\n" +                                                                            
		"    viewPos.y = ndcPos.y * rightAndTop.y;   											\n" +
		"    float scale = -viewPos.z / ZNEAR; 													\n"+
		"    viewPos.x *= scale; 																\n"+
		"    viewPos.y *= scale; 																\n"+
		"    viewPos.w = 1;                                                                     \n"+
		"    vec3 position = (viewMatrixInv * viewPos).xyz;                                		\n"+
		//				"    vec3 position = texture2D(tImage0, coor).xyz;                                		\n"+
		"    vec3 normal = texture2D( tImage1, coor).xyz;                           			\n" +
		"    vec4 prevDiff = texture2D( tImage2, coor );                         				\n" +
		"    vec4 prevSpec = texture2D( tImage3, coor );                           				\n" +
		"                                                                                    	\n" +
		//				"    vec3 lightDir = light - position ;                                               	\n" +
		//				"    vec3 lightDir = lightSelectDir(position);                                         	\n" +
		"	 vec3 lightDir;	float dist;															\n" +	
		"	 if(lightType==0){																	\n" +														
		"    	lightDir = light-position;														\n" +
		" 		dist = length(lightDir);														\n"+
		"    }                                                                                	\n" +
		"	 else {																				\n"+
		"		lightDir = light;																\n"+
		"		dist = 700;																		\n" +
		"	 }																					\n"+		
		//				"    float dist = length(lightDir);                                                   	\n" +
		"    normal = normalize(normal);                                                      	\n" +
		"    lightDir = normalize(lightDir);                                                  	\n" +
		"    vec3 eyeDir = normalize(cameraPosition-position);                                	\n" +
		"    vec3 vHalfVector = normalize(lightDir+eyeDir);                                   	\n" +                                                         
		//				"	 gl_FragData[0] = (prevDiff * 0.5)+ (((dot(normal,lightDir)+1)/2) * (lightPow.x/dist) * lightDiff)*0.5; \n" +
		//				"    gl_FragData[1] = (prevSpec * 0.5)+ (pow(((dot(normal,vHalfVector)+1)/2),0.5) * (lightPow.y/dist) * lightSpec)*0.5; \n" +
		"	 gl_FragData[0] = (prevDiff )+ (((dot(normal,lightDir)+1)/2) * (lightPow.x/dist) * lightDiff); \n" +
		"    gl_FragData[1] = (prevSpec )+ (pow(((dot(normal,vHalfVector)+1)/2),0.5) * (lightPow.y/dist) * lightSpec); \n" +
		//				"	 gl_FragData[0] = prevDiff + ((dot(normal,lightDir)+1)/2) * lightDiff; \n" +
		//				"    gl_FragData[1] = prevSpec + pow(((dot(normal,vHalfVector)+1)/2),0.5) * lightSpec; \n" +
		"}"
	};
}

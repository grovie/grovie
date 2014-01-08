package de.grovie.renderer.shader.gl2.standard;

public class GvTextureMaterialTriangleV {
	public final static String kSource = 
			"struct Light"+"\n"+
			"{"+"\n"+
			"	vec3 lightDir;		//directional light position - also the direction vector (normlized) from vertices to light position"+"\n"+
			"	vec4 lightAmb;		//directional light ambient"+"\n"+
			"	vec4 lightDif;		//directional light diffuse"+"\n"+
			"	vec4 lightSpe; 		//directional light specular"+"\n"+
			"};"+"\n"+

			"uniform vec3 cameraPos; 	//camera position world space"+"\n"+
			"uniform vec4 globalAmb;		//global ambient"+"\n"+
			"uniform Light lights[32];	//lights"+"\n"+
			"uniform int lightCount;		//number of lights"+"\n"+
			"uniform vec4 materialAmb; 	//material ambient"+"\n"+
			"uniform vec4 materialDif; 	//material diffuse"+"\n"+
			"uniform vec4 materialSpe; 	//material specular"+"\n"+
			"uniform float materialShi; 	//material shininess"+"\n"+

			"vec3 halfVector; 			// half-vector for Blinn-Phong"+"\n"+
			"vec4 diffuseColor;"+"\n"+
			"vec4 ambientColor;"+"\n"+
			"vec4 ambientColorGlobal;"+"\n"+
			"vec4 specularColor;"+"\n"+
			"float NdotL; 				//angle between world space normal and light direction"+"\n"+
			"float NdotHV; 				//cos angle between half vector and normal"+"\n"+

			"void main()"+"\n"+
			"{"+"\n"+
			"	//init irradiance terms"+"\n"+
			"	ambientColor = vec4(0,0,0,0);"+"\n"+
			"	diffuseColor = vec4(0,0,0,0);"+"\n"+
			"	specularColor = vec4(0,0,0,0);"+"\n"+
				
			"	//ambient color global - from material ambient and global ambient color"+"\n"+
			"	ambientColorGlobal = materialAmb * globalAmb;"+"\n"+

			"	for (int i= 0; i < lightCount; i++)"+"\n"+
			"	{"+"\n"+
			"		//ambient color from material ambient and light ambient colors"+"\n"+
			"		ambientColor += materialAmb * lights[i].lightAmb;"+"\n"+
				
			"		//diffuse term - compute cos of angle between normal and light direction (world space)"+"\n"+
			"		//that is the dot product of the two vectors. clamp result to [0,1]."+"\n"+
			"		NdotL = max(dot(gl_Normal,lights[i].lightDir), 0.0);"+"\n"+
					
			"		//diffuse term - diffuse color from material diffuse and light diffuse color"+"\n"+
			"		diffuseColor += NdotL * (materialDif * lights[i].lightDif);"+"\n"+
					
			"		//specular term - half vector blinn-phong"+"\n"+
			"		halfVector = lights[i].lightDir + normalize(cameraPos-gl_Vertex.xyz);"+"\n"+
				
			"		//specular term"+"\n"+
			"		if(NdotL > 0.0)"+"\n"+
			"		{"+"\n"+
			"			NdotHV = max(dot(gl_Normal,halfVector),0.0);"+"\n"+
			"			specularColor += materialSpe * lights[i].lightSpe * pow(NdotHV,materialShi);"+"\n"+
			"		}"+"\n"+
			"	}"+"\n"+

			"	//final color - diffuse term + ambient term + global ambient + specular term"+"\n"+
			"	gl_FrontColor = diffuseColor + ambientColor + ambientColorGlobal + specularColor;"+"\n"+
			"	gl_Position = ftransform(); // eq. to Proj * View * Model * gl_Vertex (in this order)"+"\n"+
			"	gl_TexCoord[0] = gl_MultiTexCoord0;"+"\n"+
			"}"
			;
}

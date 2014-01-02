struct Light
{
	vec3 lightDir;		//directional light position - also the direction vector (normlized) from vertices to light position
	vec4 lightAmb;		//directional light ambient
	vec4 lightDif;		//directional light diffuse
	vec4 lightSpe; 		//directional light specular
};

uniform vec3 cameraPos; 	//camera position world space
uniform vec4 globalAmb;		//global ambient
uniform Light lights[32];	//lights
uniform int lightCount;		//number of lights
uniform vec4 materialAmb; 	//material ambient
uniform vec4 materialDif; 	//material diffuse
uniform vec4 materialSpe; 	//material specular
uniform float materialShi; 	//material shininess

vec3 halfVector; 			// half-vector for Blinn-Phong
vec4 diffuseColor;
vec4 ambientColor;
vec4 ambientColorGlobal;
vec4 specularColor;
float NdotL; 				//angle between world space normal and light direction
float NdotHV; 				//cos angle between half vector and normal

void main()
{
	//init irradiance terms
	ambientColor = vec4(0,0,0,0);
	diffuseColor = vec4(0,0,0,0);
	specularColor = vec4(0,0,0,0);
	
	//ambient color global - from material ambient and global ambient color
	ambientColorGlobal = materialAmb * globalAmb;

	for (int i= 0; i < lightCount; i++)
	{
		//ambient color from material ambient and light ambient colors
		ambientColor += materialAmb * lights[i].lightAmb;
	
		//diffuse term - compute cos of angle between normal and light direction (world space)
		//that is the dot product of the two vectors. clamp result to [0,1].
		NdotL = max(dot(gl_Normal,lights[i].lightDir), 0.0);
		
		//diffuse term - diffuse color from material diffuse and light diffuse colors
		diffuseColor += NdotL * (materialDif * lights[i].lightDif);
		
		//specular term - half vector blinn-phong
		halfVector = lights[i].lightDir + normalize(cameraPos-gl_Vertex.xyz);
	
		//specular term
		if(NdotL > 0.0)
		{
			NdotHV = max(dot(gl_Normal,halfVector),0.0);
			specularColor += materialSpe * lights[i].lightSpe * pow(NdotHV,materialShi);
		}
	}

	//final color - diffuse term + ambient term + global ambient + specular term
	gl_FrontColor = diffuseColor + ambientColor + ambientColorGlobal + specularColor;
	gl_Position = ftransform(); // eq. to Proj * View * Model * gl_Vertex (in this order)
}
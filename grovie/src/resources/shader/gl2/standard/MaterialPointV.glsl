uniform vec3 cameraPos; 	//camera position world space
uniform vec4 globalAmb;		//global ambient
uniform vec3 lightDir;		//directional light position - also the direction vector (normlized) from vertices to light position
uniform vec4 lightAmb;		//directional light ambient
uniform vec4 lightDif;		//directional light diffuse
uniform vec4 lightSpe; 		//directional light specular
uniform vec4 materialAmb; 	//material ambient
uniform vec4 materialDif; 	//material diffuse
uniform vec4 materialSpe; 	//material specular
uniform float materialShi; 	//material shininess
vec3 normal; 				// world space normal for this vertex shader
vec3 halfVector; 			// half-vector for Blinn-Phong
vec4 diffuseColor;
vec4 ambientColor;
vec4 ambientColorGlobal;
vec4 specularColor;
float NdotL; 				//angle between world space normal and light direction
float NdotHV; 				//cos angle between half vector and normal
void main()
{
    //convert normal from model space to world space
	normal = gl_Normal;
    
	//compute cos of angle between normal and light direction (world space)
	//that is the dot product of the two vectors. clamp result to [0,1].
	NdotL = max(dot(normal,lightDir), 0.0);

	//result diffuse color from material diffuse and light diffuse colors
	diffuseColor = materialDif * lightDif;
	
	//result ambient color from material ambient and light ambient colors
	ambientColor = materialAmb * lightAmb;

	//result global ambient color from material ambient and global ambient color
	ambientColorGlobal = materialAmb * globalAmb;
	
	//half vector for specular term
	halfVector = lightDir + normalize(cameraPos-gl_Vertex.xyz);

	//computer specular term - blinn-phong
	if(NdotL > 0.0)
	{
		NdotHV = max(dot(normal,halfVector),0.0);
		specularColor = materialSpe * lightSpe * pow(NdotHV,materialShi);
	}
	else {
		specularColor= vec4(0,0,0,0);
	}

	//diffuse term + ambient term + global ambient + specular term
	gl_FrontColor = NdotL * diffuseColor + ambientColor + ambientColorGlobal + specularColor;
	gl_Position = ftransform(); // eq. to Proj * View * Model * gl_Vertex (in this order)
}
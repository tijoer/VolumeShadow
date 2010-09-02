attribute vec3 tangent;
attribute vec3 binormal;
attribute vec3 vertexViewVec;

varying	vec3 g_lightVec0;
varying	vec3 g_lightVec1;
varying	vec3 g_lightVec2;
varying	vec3 g_lightVec3;
varying	vec3 g_viewVec;

void main()
{
    gl_Position = ftransform();
    gl_TexCoord[0] = gl_MultiTexCoord0;
    
    mat3 TBN_Matrix = gl_NormalMatrix * mat3(tangent, binormal, gl_Normal);
    vec4 mv_Vertex = gl_ModelViewMatrix * gl_Vertex;
    g_viewVec = vec3(vertexViewVec);	

    vec3 lightVec0 = 0.002 * (gl_LightSource[0].position - mv_Vertex.xyz);
    g_lightVec0 = lightVec0 * TBN_Matrix; 

    vec3 lightVec1 = 0.002 * (gl_LightSource[1].position - mv_Vertex.xyz);
    g_lightVec1 = lightVec1 * TBN_Matrix; 

    vec3 lightVec2 = 0.002 * (gl_LightSource[2].position - mv_Vertex.xyz);
    g_lightVec2 = lightVec2 * TBN_Matrix; 

    vec3 lightVec3 = 0.002 * (gl_LightSource[3].position - mv_Vertex.xyz);
    g_lightVec3 = lightVec3 * TBN_Matrix;
}

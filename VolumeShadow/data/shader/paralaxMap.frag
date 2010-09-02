uniform sampler2D Normal;
uniform sampler2D base_tex;
uniform sampler2D Base_Height;

varying	vec3 g_lightVec0;
varying	vec3 g_lightVec1;
varying	vec3 g_lightVec2;
varying	vec3 g_lightVec3;
varying	vec3 g_viewVec;

void main()
{   
    //Paralax and Bump Mapping
    vec3 viewVec = normalize(g_viewVec);
    float height = texture2D(Base_Height, gl_TexCoord[0].xy).rgb;
    height = height * 0.04 - 0.05;
    vec2 newUV = gl_TexCoord[0].xy + viewVec.xy * height;
    vec4 color_base = texture2D(base_tex,newUV);
    vec3 bump = texture2D(Normal, newUV.xy).rgb * 2.0 - 1.0;
    bump = normalize(bump);
    float base = texture2D(Base_Height, newUV.xy).rgb;
    
    //Lights
    float LightAttenuation0 = clamp(1.0 - dot(g_lightVec0, g_lightVec0), 0.0, 1.0);
    vec3 lightVec0 = normalize(g_lightVec0);
    vec3 diffuse0 = clamp(dot(lightVec0, bump), 0.0, 1.0) * gl_LightSource[0].diffuse.rgb;
    vec3 specular0 = pow(clamp(dot(reflect(-viewVec, bump), lightVec0), 0.0, 1.0), 16.0) * gl_LightSource[0].specular.rgb;

    float LightAttenuation1 = clamp(1.0 - dot(g_lightVec1, g_lightVec1), 0.0, 1.0);
    vec3 lightVec1 = normalize(g_lightVec1);
    vec3 diffuse1 = clamp(dot(lightVec1, bump), 0.0, 1.0) * gl_LightSource[1].diffuse.rgb;
    vec3 specular1 = pow(clamp(dot(reflect(-viewVec, bump), lightVec1), 0.0, 1.0), 16.0) * gl_LightSource[1].specular.rgb;

    float LightAttenuation2 = clamp(1.0 - dot(g_lightVec2, g_lightVec2), 0.0, 1.0);
    vec3 lightVec2 = normalize(g_lightVec2);
    vec3 diffuse2 = clamp(dot(lightVec2, bump), 0.0, 1.0) * gl_LightSource[2].diffuse.rgb;
    vec3 specular2 = pow(clamp(dot(reflect(-viewVec, bump), lightVec2), 0.0, 1.0), 16.0) * gl_LightSource[2].specular.rgb;

    float LightAttenuation3 = clamp(1.0 - dot(g_lightVec3, g_lightVec3), 0.0, 1.0);
    vec3 lightVec3 = normalize(g_lightVec3);
    vec3 diffuse3 = clamp(dot(lightVec3, bump), 0.0, 1.0) * gl_LightSource[3].diffuse.rgb;
    vec3 specular3 = pow(clamp(dot(reflect(-viewVec, bump), lightVec3), 0.0, 1.0), 16.0) * gl_LightSource[3].specular.rgb;

    //color
    gl_FragColor.rgb = color_base
    * (  (diffuse0.rgb * base + 0.7 * specular0.rgb) * LightAttenuation0
       + (diffuse1.rgb * base + 0.7 * specular1.rgb) * LightAttenuation1
       + (diffuse2.rgb * base + 0.7 * specular2.rgb) * LightAttenuation2
       //+ (diffuse3.rgb * base + 0.7 * specular3.rgb) * LightAttenuation3
       + gl_LightSource[3].ambient.rgb// * LightAttenuation3
      );
    gl_FragColor.a = 1.0;
//TEST 2
}


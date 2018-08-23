uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;
uniform vec3 u_LightPos;
uniform vec3 u_ScatterVec;
uniform float u_ScaleFactor;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;
varying vec4 v_Color;
void main(){
    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));
    float distance = length(u_LightPos - modelViewVertex);
    vec3 lightVector = normalize(u_LightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.4);
    diffuse = diffuse * (1.0 / (0.6+ (0.0005 * distance * distance)));
    v_Color = a_Color * diffuse;



   vec4 finalPosition = vec4(a_Position[0] + u_ScatterVec[0], a_Position[1] + u_ScatterVec[1], a_Position[2] + u_ScatterVec[2], a_Position[3]);

   vec4 finalVector = u_MVPMatrix * finalPosition;
   gl_Position = finalVector;



}
uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;


uniform vec3 u_FrontLightPos;
uniform vec3 u_BackLightPos;
uniform vec3 u_LeftLightPos;
uniform vec3 u_RightLightPos;
uniform vec3 u_TopLightPos;
uniform vec3 u_BottomLightPos;


uniform vec3 u_ScatterVec;
uniform float u_ScaleFactor;
attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec3 a_Normal;
varying vec4 v_Color;

float frontLightIntensity = 0.15;
float backLightIntensity = 0.15;
float leftLightIntensity = 0.5;
float rightLightIntensity = 0.55;
float topLightIntensity = 1.0;
float bottomLightIntensity = 0.4;



float getBackLightning(vec3 modelViewVertex, vec3 modelViewNormal);
float getFrontLightning(vec3 modelViewVertex, vec3 modelViewNormal);
float getRightLightning(vec3 modelViewVertex, vec3 modelViewNormal);
float getLeftLightning(vec3 modelViewVertex, vec3 modelViewNormal);
float getTopLightning(vec3 modelViewVertex, vec3 modelViewNormal);
float getBottomLightning(vec3 modelViewVertex, vec3 modelViewNormal);

void main(){

    vec3 modelViewVertex = vec3(u_MVMatrix * a_Position);
    vec3 modelViewNormal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));

    v_Color = a_Color * (
    getFrontLightning(modelViewVertex, modelViewNormal) +
    getBackLightning(modelViewVertex, modelViewNormal) +
    getRightLightning(modelViewVertex, modelViewNormal) +
    getTopLightning(modelViewVertex, modelViewNormal)
    );


    vec4 finalPosition = vec4(a_Position[0] + u_ScatterVec[0], a_Position[1] + u_ScatterVec[1], a_Position[2] + u_ScatterVec[2], a_Position[3]);
    vec4 finalVector = u_MVPMatrix * finalPosition;
    finalVector[3] = 10.0/u_ScaleFactor;
    gl_Position = finalVector;



}

float getFrontLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_FrontLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.0001);
    diffuse = diffuse * (frontLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}

float getBackLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_BackLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.0001);
    diffuse = diffuse * (backLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}
float getRightLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_RightLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.3);
    diffuse = diffuse * (rightLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}

float getLeftLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_LeftLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.0001);
    diffuse = diffuse * (leftLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}

float getTopLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_TopLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.3);
    diffuse = diffuse * (topLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}

float getBottomLightning(vec3 modelViewVertex, vec3 modelViewNormal){

    float distance = 1.0;
    vec3 lightVector = normalize(u_BottomLightPos - modelViewVertex);
    float diffuse = max(dot(modelViewNormal, lightVector), 0.0001);
    diffuse = diffuse * (bottomLightIntensity / (1.0+ (0.0005 * distance * distance)));
    return diffuse;

}





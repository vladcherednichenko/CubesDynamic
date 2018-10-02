uniform mat4 u_MVPMatrix;
uniform mat4 u_MVMatrix;


uniform float u_ScaleFactor;
attribute vec4 a_Position;
attribute vec4 a_Color;

varying vec4 v_Color;

void main()
{

    v_Color = a_Color;

    vec4 finalVector = u_MVPMatrix * a_Position;
    finalVector[3] = 10.0/u_ScaleFactor;
    gl_Position = finalVector;

    gl_PointSize = 15.0;

}





package com.testing.vladyslav.cubes.shaders;

import android.content.Context;

import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

public class ModelShader {

    // Uniform constants
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";
    protected static final String U_MV_MATRIX = "u_MVMatrix";

    protected static final String U_FRONT_LIGHT_LOCATION = "u_FrontLightPos";
    protected static final String U_BACK_LIGHT_LOCATION = "u_BackLightPos";
    protected static final String U_LEFT_LIGHT_LOCATION = "u_LeftLightPos";
    protected static final String U_RIGHT_LIGHT_LOCATION = "u_RightLightPos";
    protected static final String U_TOP_LIGHT_LOCATION = "u_TopLightPos";
    protected static final String U_BOTTOM_LIGHT_LOCATION = "u_BottomLightPos";

    protected int uScatterPositionLocation;
    protected int uScalePositionLocation;
    //dynamic shader
    protected static final String U_LIGHT_LOCATION = "u_LightPos";

    protected static final String U_SCATTER_VEC = "u_ScatterVec";
    protected static final String U_SCALE_FACTOR = "u_ScaleFactor";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";
    protected static final String A_NORMAL = "a_Normal";


    // Attribute locations
    protected  int aPositionLocation;
    protected  int aColorLocation;
    protected  int aNormalLocation;

    protected int program;


    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public ModelShader(Context context) {


    }

    public void setUniforms(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix,
                            float[] frontLightPositionInEyeSpace,
                            float[] backLightPositionInEyeSpace,
                            float[] leftLightPositionInEyeSpace,
                            float[] rightLightPositionInEyeSpace,
                            float[] topLightPositionInEyeSpace,
                            float[] bottomLightPositionInEyeSpace){


    }

    public void setScatter(float[] scatterVector){

        glUniform3f(uScatterPositionLocation, scatterVector[0], scatterVector[1], scatterVector[2]);

    }

    public void setScaleFactor(float scaleFactor){

        glUniform1f(uScalePositionLocation, scaleFactor);

    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }

}

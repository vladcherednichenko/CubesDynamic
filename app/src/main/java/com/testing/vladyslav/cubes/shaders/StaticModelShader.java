package com.testing.vladyslav.cubes.shaders;

import android.content.Context;
import android.opengl.Matrix;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.util.ShaderHelper;
import com.testing.vladyslav.cubes.util.TextResourceReader;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform2fv;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;

public class StaticModelShader extends ModelShader{

    // Uniform locations
    private final int uMVMatrixLocation;
    private final int uMVPMatrixLocation;

    private final int uFrontLightPositionLocation;
    private final int uBackLightPositionLocation;
    private final int uLeftLightPositionLocation;
    private final int uRightLightPositionLocation;
    private final int uTopLightPositionLocation;
    private final int uBottomLightPositionLocation;


    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }
    public int getNormalAttributeLocation() {
        return aNormalLocation;
    }

    public StaticModelShader(Context context) {


        super(context);

        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.figure_static_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.figure_fragment_shader));

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uFrontLightPositionLocation = glGetUniformLocation(program, U_FRONT_LIGHT_LOCATION);
        uBackLightPositionLocation = glGetUniformLocation(program, U_BACK_LIGHT_LOCATION);
        uLeftLightPositionLocation = glGetUniformLocation(program, U_LEFT_LIGHT_LOCATION);
        uRightLightPositionLocation = glGetUniformLocation(program, U_RIGHT_LIGHT_LOCATION);
        uTopLightPositionLocation = glGetUniformLocation(program, U_TOP_LIGHT_LOCATION);
        uBottomLightPositionLocation = glGetUniformLocation(program, U_BOTTOM_LIGHT_LOCATION);

        uScatterPositionLocation = glGetUniformLocation(program, U_SCATTER_VEC);
        uScalePositionLocation = glGetUniformLocation(program, U_SCALE_FACTOR);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aNormalLocation = glGetAttribLocation(program, A_NORMAL);


    }

    public void setUniforms(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix,
                            float[] frontLightPositionInEyeSpace,
                            float[] backLightPositionInEyeSpace,
                            float[] leftLightPositionInEyeSpace,
                            float[] rightLightPositionInEyeSpace,
                            float[] topLightPositionInEyeSpace,
                            float[] bottomLightPositionInEyeSpace){


        float[] MVPMatrix = new float[16];

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // Pass in the modelview matrix.
        glUniformMatrix4fv(uMVMatrixLocation, 1, false, MVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        // Pass in the combined matrix.
        glUniformMatrix4fv(uMVPMatrixLocation, 1, false, MVPMatrix, 0);

        // Pass in the light position in eye space.
        glUniform3f(uFrontLightPositionLocation, frontLightPositionInEyeSpace[0], frontLightPositionInEyeSpace[1], frontLightPositionInEyeSpace[2]);
        glUniform3f(uBackLightPositionLocation, backLightPositionInEyeSpace[0], backLightPositionInEyeSpace[1], backLightPositionInEyeSpace[2]);

        glUniform3f(uLeftLightPositionLocation, leftLightPositionInEyeSpace[0], leftLightPositionInEyeSpace[1], leftLightPositionInEyeSpace[2]);
        glUniform3f(uRightLightPositionLocation, rightLightPositionInEyeSpace[0], rightLightPositionInEyeSpace[1], rightLightPositionInEyeSpace[2]);

        glUniform3f(uTopLightPositionLocation, topLightPositionInEyeSpace[0], topLightPositionInEyeSpace[1], topLightPositionInEyeSpace[2]);
        glUniform3f(uBottomLightPositionLocation, bottomLightPositionInEyeSpace[0], bottomLightPositionInEyeSpace[1], bottomLightPositionInEyeSpace[2]);

    }

}

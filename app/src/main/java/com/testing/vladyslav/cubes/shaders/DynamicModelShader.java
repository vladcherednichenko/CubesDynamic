package com.testing.vladyslav.cubes.shaders;

import android.content.Context;
import android.opengl.Matrix;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.util.ShaderHelper;
import com.testing.vladyslav.cubes.util.TextResourceReader;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform3f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class DynamicModelShader extends ModelShader{

    // Uniform locations
    private final int uMVMatrixLocation;
    private final int uMVPMatrixLocation;

    private final int uLightPositionLocation;


    public DynamicModelShader(Context context) {
        super(context);

        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.figure_dynamic_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.figure_fragment_shader));

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uLightPositionLocation = glGetUniformLocation(program, U_LIGHT_LOCATION);

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
        glUniform3f(uLightPositionLocation, frontLightPositionInEyeSpace[0], frontLightPositionInEyeSpace[1], frontLightPositionInEyeSpace[2]);


    }

}

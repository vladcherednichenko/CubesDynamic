package com.testing.vladyslav.cubes.shaders;

import android.content.Context;
import android.opengl.Matrix;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.util.ShaderHelper;
import com.testing.vladyslav.cubes.util.TextResourceReader;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;

public class GridShaderProgram {

    // Uniform constants
    protected static final String U_MVP_MATRIX = "u_MVPMatrix";
    protected static final String U_MV_MATRIX = "u_MVMatrix";


    protected static final String U_SCALE_FACTOR = "u_ScaleFactor";

    // Attribute constants
    protected static final String A_POSITION = "a_Position";
    protected static final String A_COLOR = "a_Color";


    // Uniform locations
    private final int uMVMatrixLocation;
    private final int uMVPMatrixLocation;


    private final int uScalePositionLocation;

    // Attribute locations
    private final int aPositionLocation;
    private final int aColorLocation;

    protected final int program;

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getColorAttributeLocation() {
        return aColorLocation;
    }


    public GridShaderProgram(Context context) {

        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.grid_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.grid_fragment_shader));

        uMVMatrixLocation = glGetUniformLocation(program, U_MV_MATRIX);
        uMVPMatrixLocation = glGetUniformLocation(program, U_MVP_MATRIX);

        uScalePositionLocation = glGetUniformLocation(program, U_SCALE_FACTOR);

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);


    }

    public void setUniforms(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix){


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


    }


    public void setScaleFactor(float scaleFactor){

        glUniform1f(uScalePositionLocation, scaleFactor);

    }

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}

package com.testing.vladyslav.cubes.objects;

import android.content.Context;
import android.opengl.GLES20;

import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.shaders.BackGroundShader;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class Background {

    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int vertexNumber = 0;

    private VertexArray vertexPosArray;
    private VertexArray colorPosArray;

    private int vertexBufferPositionIdx = 0;
    private int vertexBufferColorIdx = 0;


    private float vertexDataArray[] = {
            0.0f, 0.0f, 0.0f,
            1.0f, 1.0f, 0.0f,
            -1.0f, 1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f, 1.0f, 0.0f
    };

    private float colorDataArray[] = {
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f,
            1.0f, 1.0f, 1.0f, 0.0f
    };

    public Background(){

        vertexPosArray = new VertexArray(vertexDataArray);
        colorPosArray = new VertexArray(colorDataArray);
        vertexNumber = vertexDataArray.length / POSITION_COMPONENT_COUNT;

    }

    public void bindAttributesData(){

        glDeleteBuffers(2, new int[]{vertexBufferPositionIdx, vertexBufferColorIdx}, 0);

        final int buffers[] = new int[2];
        glGenBuffers(2, buffers, 0);

        vertexBufferPositionIdx = buffers[0];
        vertexBufferColorIdx = buffers[1];

        vertexPosArray.bindBufferToVBO(vertexBufferPositionIdx);
        colorPosArray.bindBufferToVBO(vertexBufferColorIdx);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vertexPosArray = null;
        colorPosArray = null;

    }

    public void draw(BackGroundShader shader){

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferPositionIdx);
        glEnableVertexAttribArray(shader.getPositionAttributeLocation());
        glVertexAttribPointer(shader.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);


        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferColorIdx);
        glEnableVertexAttribArray(shader.getColorAttributeLocation());
        glVertexAttribPointer(shader.getColorAttributeLocation(), COLOR_COORDINATES_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_TRIANGLE_FAN, 0, vertexNumber);

    }

}

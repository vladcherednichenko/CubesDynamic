package com.testing.vladyslav.cubes.data;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glVertexAttribPointer;
import static com.testing.vladyslav.cubes.Constants.BYTES_PER_FLOAT;

public class  VertexArray {

    private FloatBuffer floatBuffer;

    public VertexArray(float[] vertexData){
        floatBuffer = ByteBuffer
                .allocateDirect(vertexData.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);

        floatBuffer.position(0);
    }

    public void setVertexAttribPointer(int dataOffset, int attributeLocation, int componentCount, int stride){

        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);

    }

    public void bindBufferToVBO(int VBOPointer){

        glBindBuffer(GL_ARRAY_BUFFER, VBOPointer);
        glBufferData(GL_ARRAY_BUFFER, floatBuffer.capacity()* BYTES_PER_FLOAT, floatBuffer, GLES20.GL_STATIC_DRAW);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        floatBuffer.limit(0);
        floatBuffer = null;

    }

}

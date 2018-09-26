package com.testing.vladyslav.cubes.shaders;

import android.content.Context;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.util.ShaderHelper;
import com.testing.vladyslav.cubes.util.TextResourceReader;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glUseProgram;

public class BackGroundShader {

    private static String A_POSITION = "a_Position";
    private static String A_COLOR = "a_Color";

    private final int aColorLocation;
    private final int aPositionLocation;

    private int program;

    public int getColorAttributeLocation(){return  aColorLocation;}
    public int getPositionAttributeLocation(){return aPositionLocation;}

    public BackGroundShader(Context context){

        program = ShaderHelper.buildProgram(
                TextResourceReader.readTextFileFromResource(context, R.raw.background_vertex_shader),
                TextResourceReader.readTextFileFromResource(context, R.raw.background_fragment_shader));

        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aColorLocation = glGetAttribLocation(program, A_COLOR);


    }

    public void useProgram(){
        glUseProgram(program);
    }


}

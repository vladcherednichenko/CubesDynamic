package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.animation.Animator;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.programs.ShaderProgram;
import com.testing.vladyslav.cubes.util.Color;

import java.util.ArrayList;
import java.util.HashMap;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class FigureBuilder {

    private static final String TAG = "FigureBuilder";

    private float cubeSize = 1;
    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int NORMAL_COMPONENT_COUNT = 3;
    private int STRIDE = 0;


    public static final HashMap<Integer, String> colorCodeToHex = new HashMap<Integer, String>(){
        {
            put(248, "#b80b11");
            put(247, "#ff6905");
            put(246, "#ffb805");
            put(245, "#62b013");
            put(244, "#007800");
            put(243, "#00b8b8");
            put(242, "#0e94ed");
            put(241, "#2424a3");
            put(249, "#8032cf");
            put(250, "#ff6bb0");
            put(251, "#d9bb96");
            put(252, "#b07e3e");
            put(253, "#6e4c3c");
            put(255, "#737373");
            put(240, "#333333");
            put(254, "#ffffff");
        }};


    private int model[] = {-1,-1,-1,-1,-1,-1,-1,244,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    //private int model[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,254,-1,254,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,254,-1,254,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,254,-1,254,-1,-1,-1,-1,-1,-1,-1,253,253,253,-1,-1,254,240,254,-1,-1,254,240,254,-1,-1,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,253,240,-1,-1,253,253,253,-1,-1,253,253,253,-1,-1,254,254,254,-1,-1,240,240,240,-1,-1,-1,254,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,253,253,253,-1,-1,253,240,253,-1,-1,253,253,253,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,-1,-1,253,240,253,-1,-1,253,253,253,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,253,-1,-1,-1,-1,253,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,253,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,254,-1,-1,-1,-1,240,-1,-1,-1,-1,253,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,253,-1,-1,240,240,253,240,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    //private int model[] = {-1,-1,-1,-1,-1,-1,255,-1,255,255,-1,255,-1,-1,-1,-1,255,-1,-1,255,-1,-1,255,-1,-1,255,-1,-1,255,-1,-1,255,-1,-1,-1,-1,-1,255,-1,-1,251,-1,251,252,251,252,252,252,-1,255,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,251,252,251,252,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,240,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,-1,251,252,251,251,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,251,251,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,251,251,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,251,251,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    //private int model[] = {-1,240,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,-1,240,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,-1,251,251,251,251,251,251,251,-1,-1,-1,-1,-1,-1,-1,251,251,251,251,251,251,251,-1,-1,-1,-1,-1,-1,-1,251,251,251,251,251,251,251,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,252,252,252,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,252,252,252,252,252,252,252,251,-1,-1,-1,-1,-1,-1,252,252,252,252,252,252,252,252,251,-1,-1,-1,-1,-1,252,252,252,252,252,252,252,251,-1,-1,-1,-1,-1,-1,252,252,252,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,252,252,252,252,252,251,251,-1,-1,-1,-1,252,252,252,252,252,252,252,252,252,251,-1,-1,-1,-1,-1,252,252,252,252,252,252,252,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,251,-1,-1,-1,-1,252,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,240,240,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,252,252,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,252,252,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,251,-1,-1,251,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,251,-1,-1,251,251,-1,-1,-1,-1,-1,-1,251,251,251,-1,-1,-1,-1,251,251,251,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,251,-1,-1,-1,-1,251,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};


//    private int sizeX = 9;
//    private int sizeZ = 5;

    private int sizeX = 11;
    private int sizeZ = 3;

//    private int sizeX = 5;
//    private int sizeZ = 14;



    private int sizeY;


    private VertexArray vertexPosArray;
    private VertexArray vertexColorArray;
    private VertexArray vertexNormalArray;

    private float[] vertexPositionData;
    private float[] vertexColorData;
    private float[] vertexNormalData;

    private int vertexBufferPositionIdx = 0;
    private int vertexBufferColorIdx = 0;
    private int vertexBufferNormalIdx = 0;

    private ArrayList<Cube> cubes;
    private ArrayList<Point> cubeCenters = new ArrayList<>();

    //private int cubeNumber = 71;
    private int cubeNumber = 1;
    //private int cubeNumber = 157;

    private int vertexDataOffset = 0;
    private int vertexColorDataOffset = 0;
    private int vertexNormalDataOffset = 0;

    public FigureBuilder(){


        cubeCenters.add(new Point (0f, 0f, 0f));

        buildFigure(cubeCenters);


    }

    private void buildFigure(ArrayList<Point> cubeCenters){

        cubes = new ArrayList<>();

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        for (Point center: cubeCenters){

            Color color = new Color("#f4b942");
            Cube cube = new Cube(center, color);

            cubes.add(cube);
            appendCube(cube);

        }

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);

    }

    public void addNewCube(Point center){

        for (Point oldCenter: cubeCenters){

            if (center.x == oldCenter.x &&
                    center.y == oldCenter.y &&
                    center.z == oldCenter.z){
                return;
            }
        }

        cubeNumber++;
        cubeCenters.add(center);
        buildFigure(cubeCenters);
        bindAttributesData();

    }

    public Point getCubeCenter(){

        if(cubes != null && cubes.size()!=0)
            return cubes.get(0).center;

        else return null;

        //return cubeCenters;

    }

    public ArrayList<Point> getCubeCenters(){

        return cubeCenters;

    }


    private void appendCube(Cube cube){


        for (float f: cube.cubePositionData){
            vertexPositionData[vertexDataOffset++] = f;
        }

        for (float f: cube.cubeNormalData){
            vertexNormalData[vertexNormalDataOffset++] = f;
        }

        for (float f: cube.cubeColorData){
            vertexColorData[vertexColorDataOffset++] = f;
        }

    }

    public void bindAttributesData(){


        final int buffers[] = new int[3];
        glGenBuffers(3, buffers, 0);

        vertexBufferPositionIdx = buffers[0];
        vertexBufferColorIdx = buffers[1];
        vertexBufferNormalIdx = buffers[2];

        vertexPosArray.bindBufferToVBO(vertexBufferPositionIdx);
        vertexColorArray.bindBufferToVBO(vertexBufferColorIdx);
        vertexNormalArray.bindBufferToVBO(vertexBufferNormalIdx);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vertexPosArray = null;
        vertexColorArray = null;
        vertexNormalArray = null;


    }



    public void draw(ShaderProgram shader){


        //draw figure
        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferPositionIdx);
        glEnableVertexAttribArray(shader.getPositionAttributeLocation());
        glVertexAttribPointer(shader.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);


        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferColorIdx);
        glEnableVertexAttribArray(shader.getColorAttributeLocation());
        glVertexAttribPointer(shader.getColorAttributeLocation(), COLOR_COORDINATES_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);


        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferNormalIdx);
        glEnableVertexAttribArray(shader.getNormalAttributeLocation());
        glVertexAttribPointer(shader.getNormalAttributeLocation(), NORMAL_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);


        glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeDataHolder.getInstance().sizeInVertex * cubeNumber);

    }




}

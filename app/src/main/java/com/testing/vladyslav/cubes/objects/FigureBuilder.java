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
            put(248, "#E4002B"); //red
            put(247, "#FF8200"); //orange
            put(246, "#FEDB00"); //yellow
            put(245, "#62b013"); //light green
            put(244, "#00843D"); //green
            put(243, "#00b8b8"); //turquoise
            put(242, "#41B6E6"); //light blue
            put(241, "#003087"); //blue
            put(249, "#753BBD"); //violet
            put(250, "#F57EB6"); //pink
            put(251, "#FCD299"); //tan
            put(252, "#C88242"); //light brown
            put(253, "#693F23"); //brown
            put(255, "#A2AAAD"); //grey
            put(240, "#333333"); //black
            put(254, "#ffffff"); //white
            put(238, "#333333"); //black
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

    private ArrayList<Cube> reservedCubes;
    private ArrayList<Cube> cubes;
    private ArrayList<Point> reservedCubeCenters = new ArrayList<>();
    private ArrayList<Point> cubeCenters = new ArrayList<>();

    //private int cubeNumber = 71;
    private int cubeNumber = 0;
    //private int cubeNumber = 157;

    private int vertexDataOffset = 0;
    private int vertexColorDataOffset = 0;
    private int vertexNormalDataOffset = 0;

    public ArrayList<Point> getCubeCenters(){ return cubeCenters;}

    public FigureBuilder(){


//        cubeCenters.add(new Point (0f, 0f, 0f));
//
//        cubeCenters.add(new Point (0f, 0f, 1f));
//
//        cubeCenters.add(new Point (0f, 0f, -1f));
//
//        cubeCenters.add(new Point (0f, 1f, 0f));
//
//        cubeCenters.add(new Point (0f, -1f, 0f));
//
//        cubeCenters.add(new Point (1f, 0f, 0f));
//
//        cubeCenters.add(new Point (-1f, 0f, 0f));


        cubes = new ArrayList<>();
        reservedCubes = new ArrayList<>();

        for (Point center: cubeCenters){

            Color color = new Color("#f4b942");
            Cube cube = new Cube(center, color);

            cubes.add(cube);
            reservedCubes.add(cube);


        }

        buildFigure(cubes);


    }

    private void buildFigure(ArrayList<Cube> cubes){

        if(cubeNumber<=0) return;

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        for (Cube cube: cubes){

            appendCube(cube);

        }

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);

    }

    public void addNewCube(Point center, int colorIndex){

        if(center == null) return;

        for (Point oldCenter: cubeCenters){

            if (center.x == oldCenter.x &&
                    center.y == oldCenter.y &&
                    center.z == oldCenter.z){
                return;
            }
        }

        cubeNumber++;
        cubeCenters.add(center);
        reservedCubeCenters = new ArrayList<>(cubeCenters);


        //buildFigure(cubeCenters);


        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        Color color = new Color(colorCodeToHex.get(colorIndex));
        Cube newCube = new Cube(center, color);
        cubes.add(newCube);
        reservedCubes = new ArrayList<>(cubes);



        for (Cube cube: cubes){

            appendCube(cube);

        }

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);

        bindAttributesData();


    }

    public void highliteCube (Point center){

        cubes = new ArrayList<>();

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        for (Point cubeCenter: cubeCenters){

            Color color;

            if(cubeCenter.equals(center)){
                color = new Color("#ff0000");
            }else
            color = new Color("#f4b942");

            Cube cube = new Cube(cubeCenter, color);

            cubes.add(cube);
            appendCube(cube);

        }

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);


        bindAttributesData();

    }

    public void forward(){

        if(reservedCubes.size()<=0 || reservedCubes.size() == cubes.size()){
            return;
        }

        cubeCenters.add(reservedCubeCenters.get(cubeCenters.size()));
        cubes.add(reservedCubes.get(cubes.size()));
        cubeNumber++;
        buildFigure(cubes);

        bindAttributesData();


    }

    public void backward(){

        if(cubes.size()>0){
            cubes.remove(cubes.size()-1);
            cubeCenters.remove(cubeCenters.size()-1);
            cubeNumber --;

            buildFigure(cubes);
            bindAttributesData();

        }


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


        if(cubeNumber <=0){
            return;
        }

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

        if(cubeNumber <= 0){
            return;
        }

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
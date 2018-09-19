package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.programs.GridShaderProgram;
import com.testing.vladyslav.cubes.util.PixioColor;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class GridBuilder {

    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int STRIDE = 0;
    private float cubeSize = 1f;


    public int maxGridSize = 24;
    public int minGridSize = 10;
    public int gridSize = 10;


    private float gridHeight = -1.5f;
    private int vertexNumber;

    private VertexArray vertexPosArray;
    private VertexArray vertexColorArray;

    private float[] vertexPositionData;
    private float[] vertexColorData;

    private int vertexBufferPositionIdx = 0;
    private int vertexBufferColorIdx = 0;

    private int vertexDataOffset = 0;
    private int vertexColorDataOffset = 0;


    private ArrayList<PixioPoint> tileCenters;
    private ArrayList<Line> grid;


    public ArrayList<PixioPoint> getTileCenters(){return tileCenters; }
    public boolean isGridMinimumSize(){return gridSize == minGridSize;}
    public boolean isGridMaximumSize(){return gridSize == maxGridSize;}

    public void expandGrid(){

        if(!isGridMaximumSize()){

            gridSize+=2;
            buildGrid(gridSize, gridHeight);
            buildTiles(gridSize, gridHeight);
            bindAttributesData();

        }

    }

    public void narrowGrid(){

        if(!isGridMinimumSize()){

            gridSize-=2;
            buildGrid(gridSize, gridHeight);
            buildTiles(gridSize, gridHeight);
            bindAttributesData();

        }

    }

    public void setGridSize(int size){

        if(size > maxGridSize) size = maxGridSize;
        if(size < minGridSize) size = minGridSize;

        gridSize = size;

        buildGrid(gridSize, gridHeight);
        buildTiles(gridSize, gridHeight);
        bindAttributesData();

    }

    public GridBuilder(float height){

        this.gridHeight = height;

    }

    public void build(){
        buildGrid(gridSize, gridHeight);
        buildTiles(gridSize, gridHeight);
    }

    private void buildGrid(int size, float height){

        vertexPositionData = new float[(size+1) * 4 * POSITION_COMPONENT_COUNT];
        vertexColorData = new float[(size+1) * 4 * COLOR_COORDINATES_COMPONENT_COUNT];

        resetOffsets();

        PixioColor color = new PixioColor("#828282");

        PixioPoint defaultHorizontalStartPoint = new PixioPoint(0f, height, -size / 2f);
        PixioPoint defaultHorizontalEndPoint = new PixioPoint(0f, height, size / 2f);

        PixioPoint defaultVerticalStartPoint = new PixioPoint(-size / 2f, height, 0f);
        PixioPoint defaultVerticalEndPoint = new PixioPoint(size / 2f, height, 0f);


        grid = new ArrayList<>();

        int linesInOneRow = (size+1);

        defaultVerticalStartPoint.translateZ((-1) * (float)size / 2f);
        defaultVerticalEndPoint.translateZ((-1) * (float)size / 2f);

        defaultHorizontalStartPoint.translateX((-1) * (float)size / 2f);
        defaultHorizontalEndPoint.translateX((-1) * (float)size / 2f);


        //PixioPoint default

        //create horizontal lines
        for (int i = 0; i< linesInOneRow; i++){

            appendLine(new Line(defaultHorizontalStartPoint.clone(), defaultHorizontalEndPoint.clone(), color));
            defaultHorizontalEndPoint.translateX(1f);
            defaultHorizontalStartPoint.translateX(1f);

        }

        //create vertical lines
        for (int i = 0; i< linesInOneRow; i++){

            appendLine(new Line(defaultVerticalStartPoint, defaultVerticalEndPoint, color));
            defaultVerticalEndPoint.translateZ(1f);
            defaultVerticalStartPoint.translateZ(1f);

        }

        vertexNumber = vertexPositionData.length / POSITION_COMPONENT_COUNT;

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);

        vertexColorData = null;
        vertexPositionData = null;

    }

    private void buildTiles(float gridSize, float gridHeight){

        tileCenters = new ArrayList<>();

        PixioPoint defaultTilePosition = new PixioPoint(-(float)gridSize/2 + cubeSize/2, gridHeight - cubeSize / 2, -(float)gridSize/2 + cubeSize/2);

        //tileCenters.add(defaultTilePosition.clone());

        for (int i = 0; i< gridSize; i++){


            for (int j = 0; j< gridSize; j++){

                PixioPoint center = defaultTilePosition.clone();
                center.translateX(cubeSize * j);

                tileCenters.add(center);

            }

            defaultTilePosition.translateZ(cubeSize);

        }


    }

    public void bindAttributesData(){

        glDeleteBuffers(2, new int[]{vertexBufferPositionIdx, vertexBufferColorIdx}, 0);

        final int buffers[] = new int[2];
        glGenBuffers(2, buffers, 0);

        vertexBufferPositionIdx = buffers[0];
        vertexBufferColorIdx = buffers[1];

        vertexPosArray.bindBufferToVBO(vertexBufferPositionIdx);
        vertexColorArray.bindBufferToVBO(vertexBufferColorIdx);

        glBindBuffer(GL_ARRAY_BUFFER, 0);

        vertexPosArray = null;
        vertexColorArray = null;


    }

    public void lowerGrid(){

        buildGrid(gridSize, gridHeight-=1f);
        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        bindAttributesData();

    }

    public void raiseGrid(){

        buildGrid(gridSize, gridHeight+=1f);
        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        bindAttributesData();

    }

    public void draw(GridShaderProgram shader){

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferPositionIdx);
        glEnableVertexAttribArray(shader.getPositionAttributeLocation());
        glVertexAttribPointer(shader.getPositionAttributeLocation(), POSITION_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);


        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferColorIdx);
        glEnableVertexAttribArray(shader.getColorAttributeLocation());
        glVertexAttribPointer(shader.getColorAttributeLocation(), COLOR_COORDINATES_COMPONENT_COUNT, GLES20.GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_LINES, 0, vertexNumber);

    }

    private void appendLine(Line line){

        grid.add(line);

        for (float f: line.linePositionData){
            vertexPositionData[vertexDataOffset++] = f;
        }

        for (float f: line.lineColorData) {
            vertexColorData[vertexColorDataOffset++] = f;
        }

    }

    private void resetOffsets(){

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;

    }

    private class Line{

        private String TAG = "Line";

        int NUMBER_OF_POINTS = 2;

        int POSITION_ARRAY_SIZE = NUMBER_OF_POINTS * POSITION_COMPONENT_COUNT;
        int COLOR_ARRAY_SIZE = NUMBER_OF_POINTS * COLOR_COORDINATES_COMPONENT_COUNT;

        public float[] linePositionData;
        public float[] lineColorData;

        private PixioPoint startPoint;
        private PixioPoint endPoint;

        public Line(PixioPoint start, PixioPoint end, PixioColor color){
            this.startPoint = start;
            this.endPoint = end;


            linePositionData = new float[POSITION_ARRAY_SIZE];

            linePositionData[0] = start.x;
            linePositionData[1] = start.y;
            linePositionData[2] = start.z;

            linePositionData[3] = end.x;
            linePositionData[4] = end.y;
            linePositionData[5] = end.z;

            lineColorData = new float[COLOR_ARRAY_SIZE];

            lineColorData[0] = color.RED;
            lineColorData[1] = color.GREEN;
            lineColorData[2] = color.BLUE;
            lineColorData[3] = 1f;

            lineColorData[4] = color.RED;
            lineColorData[5] = color.GREEN;
            lineColorData[6] = color.BLUE;
            lineColorData[7] = 1f;

            checkIfEverythingAllRight();

        }

        private void checkIfEverythingAllRight(){

            if (
                    lineColorData[0] == 0 ||
                            lineColorData[COLOR_ARRAY_SIZE-1] == 0 ||
                            linePositionData[0] == 0 ||
                            linePositionData[POSITION_ARRAY_SIZE-1] == 0
                    ){

                //Log.w(TAG, "position or color data is missing");

            }

        }

    }



}

package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.Settings;
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

    private int dimensionsNumber = 4;
    private float[] gridDimensions = new float[dimensionsNumber];

    private final int gridMinX = 0;
    private final int gridMaxX = 1;

    private final int gridMinZ = 2;
    private final int gridMaxZ = 3;

    public int maxGridSize = Settings.unlimitedGrid ? Settings.unlimitedGridSize : Settings.maximumGridSize;
    public int minGridSize = Settings.minimumGridSize;
    public int gridSize = minGridSize;


    private float gridHeight = Settings.gridHeight;
    private PixioPoint gridCenter = new PixioPoint(0f, gridHeight, 0f);
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

//    public void expandGrid(){
//
//        if(!isGridMaximumSize()){
//
//            gridSize+=2;
//            buildGrid(gridSize, gridCenter);
//            buildTiles(gridSize, gridCenter);
//            bindAttributesData();
//
//        }
//
//    }
//
//    public void narrowGrid(){
//
//        if(!isGridMinimumSize()){
//
//            gridSize-=2;
//            buildGrid(gridSize, gridCenter);
//            buildTiles(gridSize, gridCenter);
//            bindAttributesData();
//
//        }
//
//    }

    public void updateGrid(float minX, float maxX, float minZ, float maxZ){

        if(minX == gridDimensions[gridMinX] || maxX == gridDimensions[gridMaxX] || minZ == gridDimensions[gridMinZ] || maxZ == gridDimensions[gridMaxZ]){

            if(gridSize >=maxGridSize){

                gridSize = maxGridSize;


            }else{

                gridSize+=2;
                buildGrid(gridSize, gridCenter);
                buildTiles(gridSize, gridCenter);
                bindAttributesData();

            }

        }

    }


    public void setGridSize(int size){

        if(size > maxGridSize) size = maxGridSize;
        if(size < minGridSize) size = minGridSize;

        gridSize = size;

        buildGrid(gridSize, gridCenter);
        buildTiles(gridSize, gridCenter);
        bindAttributesData();

    }

    public GridBuilder(){



    }

    public void build(){
        buildGrid(gridSize, gridCenter);
        buildTiles(gridSize, gridCenter);
    }



    private void buildGrid(int size, PixioPoint center){

        vertexPositionData = new float[(size+1) * 4 * POSITION_COMPONENT_COUNT];
        vertexColorData = new float[(size+1) * 4 * COLOR_COORDINATES_COMPONENT_COUNT];

        resetOffsets();

        PixioColor color = new PixioColor(Settings.gridColor);

        PixioPoint defaultHorizontalStartPoint = new PixioPoint(center.x - size/2, center.y, center.z - size / 2f);
        PixioPoint defaultHorizontalEndPoint = new PixioPoint(center.x - size/2, center.y, center.z + size / 2f);

        PixioPoint defaultVerticalStartPoint = new PixioPoint(center.x -size / 2f, center.y, center.z - size/2);
        PixioPoint defaultVerticalEndPoint = new PixioPoint(center.x + size / 2f, center.y, center.z - size/2);



        grid = new ArrayList<>();
        int linesInOneRow = (size+1);


        for (int i = 0; i< linesInOneRow; i++){

            //create horizontal lines
            appendLine(new Line(defaultHorizontalStartPoint.clone(), defaultHorizontalEndPoint.clone(), color));
            defaultHorizontalEndPoint.translateX(1f);
            defaultHorizontalStartPoint.translateX(1f);

            //create vertical lines
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

    private void buildTiles(float gridSize, PixioPoint gridCenter){

        tileCenters = new ArrayList<>();

        PixioPoint defaultTilePosition = new PixioPoint( gridCenter.x - gridSize/2 + cubeSize/2, gridCenter.y - cubeSize / 2, gridCenter.z-gridSize/2 + cubeSize/2);

        gridDimensions[gridMinX] = defaultTilePosition.x;
        gridDimensions[gridMinZ] = defaultTilePosition.z;

        for (int i = 0; i< gridSize; i++){


            for (int j = 0; j< gridSize; j++){

                PixioPoint center = defaultTilePosition.clone();
                center.translateX(cubeSize * j);

                tileCenters.add(center);

                gridDimensions[gridMaxX] = center.x;
                gridDimensions[gridMaxZ] = center.z;

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

//    public void lowerGrid(){
//
//        gridCenter.translateY(-1f);
//        buildGrid(gridSize, gridCenter);
//        vertexPosArray = new VertexArray(vertexPositionData);
//        vertexColorArray = new VertexArray(vertexColorData);
//        bindAttributesData();
//
//    }
//
//    public void raiseGrid(){
//
//        gridCenter.translateY(-1f);
//        buildGrid(gridSize, gridCenter);
//        vertexPosArray = new VertexArray(vertexPositionData);
//        vertexColorArray = new VertexArray(vertexColorData);
//        bindAttributesData();
//
//    }

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

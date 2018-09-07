package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.programs.ShaderProgram;
import com.testing.vladyslav.cubes.util.PixioColor;
import com.testing.vladyslav.cubes.util.UserModelHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class FigureBuilder {

    private static final String TAG = "FigureBuilder";

    private float cubeSize = 1f;
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


    private VertexArray vertexPosArray;
    private VertexArray vertexColorArray;
    private VertexArray vertexNormalArray;

    private float[] vertexPositionData;
    private float[] vertexColorData;
    private float[] vertexNormalData;

    private int vertexDataOffset = 0;
    private int vertexColorDataOffset = 0;
    private int vertexNormalDataOffset = 0;

    private int vertexBufferPositionIdx = 0;
    private int vertexBufferColorIdx = 0;
    private int vertexBufferNormalIdx = 0;

    private int maxModelSize = 24;
    private float [] modelDimensions = new float [9];

    private int sizeX = 0;
    private int sizeY = 1;
    private int sizeZ = 2;

    private int minX = 3;
    private int maxX = 4;

    private int minZ = 5;
    private int maxZ = 6;

    private int minY = 7;
    private int maxY = 8;

    private ArrayList<Cube> reservedCubes;
    private ArrayList<Cube> cubes;
    private ArrayList<PixioPoint> reservedCubeCenters;
    private ArrayList<PixioPoint> cubeCenters;

    private int cubeNumber = 0;

    private GridBuilder gridBuilder;

    public ArrayList<PixioPoint> getCubeCenters(){ return cubeCenters;}

    public void setGridBuilder(GridBuilder grid){this.gridBuilder = grid;}


    public UserModel getModel(){

        UserModel model = new UserModel();
        model.setCubeNumber(cubeNumber);
        model.setSizeY(sizeY);
        model.setSizeX(sizeX);
        model.setSizeZ(sizeZ);
        model.setCubes(UserModelHelper.getStringModelForm(cubes));

        return model;

    }

    public FigureBuilder(){

        cubes = new ArrayList<>();
        reservedCubes = new ArrayList<>();

        cubeCenters = new ArrayList<>();
        reservedCubeCenters = new ArrayList<>();

        buildFigure(cubes);

    }

    public void setModel(UserModel model){

        this.sizeX = model.getSizeX() == null? 0: model.getSizeX();
        this.sizeY = model.getSizeY() == null? 0: model.getSizeY();
        this.sizeZ = model.getSizeZ() == null? 0: model.getSizeZ();

        this.cubeNumber = model.getCubeNumber() == null? 0: model.getCubeNumber();

        this.cubes = UserModelHelper.getCubesModelForm(model.getCubes() == null? "" : model.getCubes());
        this.reservedCubes = new ArrayList<>(cubes);
        this.cubeCenters = new ArrayList<>();

        for (Cube cube: cubes){
            cubeCenters.add(cube.center);
        }

        this.reservedCubeCenters = new ArrayList<>(cubeCenters);

        buildFigure(cubes);

        bindAttributesData();

    }

    private void buildFigure(ArrayList<Cube> cubes){

        if(cubeNumber<=0) return;

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        clearFigureDimensions();

        for (Cube cube: cubes){

            cube.createCubeData();
            appendCube(cube);
            cube.releaseCubeData();

            updateFigureDimensions(cube);

        }

        gridBuilder.setGridSize(Math.round(getFigureMaxXZDimen()+1) *2);

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);

        vertexPositionData = null;
        vertexNormalData = null;
        vertexColorData = null;

    }

    private float getFigureMaxXZDimen(){

        return Math.max(
                Math.max(Math.abs(modelDimensions[maxX]), Math.abs(modelDimensions[minX])),
                Math.max(Math.abs(modelDimensions[maxZ]), Math.abs(modelDimensions[minZ])));

    }

    public float getFigureMaxXYZDimen(){

        return Math.max(gridBuilder.gridSize, maxY - minY);

    }

    public PixioPoint getFigureCenter(){

        return new PixioPoint(
                (Math.abs(modelDimensions[maxX]) - Math.abs(modelDimensions[minX]))/2,
                (Math.abs(modelDimensions[maxY]) - Math.abs(modelDimensions[minY]))/2,
                (Math.abs(modelDimensions[maxZ]) - Math.abs(modelDimensions[minZ]))/2
        );

    }

    private void updateFigureDimensions(Cube cube){


        //count figure size
        if(cube.center.x > modelDimensions[maxX]){
             modelDimensions[maxX] = cube.center.x;
        }

        if(cube.center.x < modelDimensions[minX]){
            modelDimensions[minX] = cube.center.x;
        }

        if(cube.center.y > modelDimensions[maxY]){
            modelDimensions[maxY] = cube.center.y;
        }

        if(cube.center.y < modelDimensions[minY]){
            modelDimensions[minY] = cube.center.y;
        }

        if(cube.center.z > modelDimensions[maxZ]){
            modelDimensions[maxZ] = cube.center.z;
        }

        if(cube.center.z < modelDimensions[minZ]){
            modelDimensions[minZ] = cube.center.z;
        }

    }

    private void clearFigureDimensions(){
        for (int i = 0; i<modelDimensions.length; i++){
            modelDimensions[i] = 0;
        }
    }

    public void addNewCubeClicked(PixioPoint center, int colorIndex){

    }

    public void deleteCubeClicked(PixioPoint center){

    }

    public void paintCubeClicked(PixioPoint center, int colorIndex){

    }

    public void addNewCube(PixioPoint center, final int colorIndex){

        if(center == null) return;

        //check if block if out of figure bounds
        if(center.y - modelDimensions[minY] >= maxModelSize || Math.abs(center.x)> gridBuilder.maxGridSize/2 ||Math.abs(center.z)>gridBuilder.maxGridSize /2){
            return;
        }

        for (PixioPoint oldCenter: cubeCenters){
            if (center.equals(oldCenter)){
                return;
            }
        }

        cubeNumber++;
        cubeCenters.add(center);
        reservedCubeCenters = new ArrayList<>(cubeCenters);

        cubes.add(new Cube(center, new PixioColor(colorCodeToHex.get(colorIndex))));
        reservedCubes = new ArrayList<>(cubes);


        buildFigure(cubes);

        bindAttributesData();


    }

    public void deleteCube(PixioPoint center){

        if(center == null) return;
        Iterator<PixioPoint> pointIterator = cubeCenters.iterator();

        cubeNumber--;

        while(pointIterator.hasNext()){

            PixioPoint point = pointIterator.next();
            if(point.equals(center)){

                pointIterator.remove();

                reservedCubeCenters = new ArrayList<>(cubeCenters);

                break;

            }

        }


        Iterator<Cube> iterator = cubes.iterator();

        while(iterator.hasNext()){

            Cube cube = iterator.next();
            if(cube.center.equals(center)){

                iterator.remove();

                reservedCubes = new ArrayList<>(cubes);

                buildFigure(cubes);

                bindAttributesData();

                break;

            }

        }


    }

    public void changeCubeColor(PixioPoint center, int colorIndex){

        if(center == null) return;

        Iterator<Cube> iterator = cubes.iterator();

        while(iterator.hasNext()){

            Cube cube = iterator.next();
            if(cube.center.equals(center)){

                iterator.remove();
                cubes.add(new Cube(center, new PixioColor(colorCodeToHex.get(colorIndex))));

                reservedCubes = new ArrayList<>(cubes);
                reservedCubeCenters = new ArrayList<>(cubeCenters);

                buildFigure(cubes);

                bindAttributesData();

                break;

            }

        }

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

        glDeleteBuffers(3, new int[]{vertexBufferPositionIdx, vertexBufferColorIdx, vertexBufferNormalIdx}, 0);

        final int buffers[] = new int[3];
        glGenBuffers(3, buffers, 0);
        glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        vertexBufferPositionIdx = buffers[0];
        vertexBufferColorIdx = buffers[1];
        vertexBufferNormalIdx = buffers[2];

        vertexPosArray.bindBufferToVBO(vertexBufferPositionIdx);
        vertexColorArray.bindBufferToVBO(vertexBufferColorIdx);
        vertexNormalArray.bindBufferToVBO(vertexBufferNormalIdx);




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

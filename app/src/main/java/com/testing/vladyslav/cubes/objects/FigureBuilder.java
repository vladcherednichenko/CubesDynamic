package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.Settings;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.objects.userActionsManagement.FigureChangesManager;
import com.testing.vladyslav.cubes.programs.ShaderProgram;
import com.testing.vladyslav.cubes.util.PixioColor;
import com.testing.vladyslav.cubes.util.PixioHelper;
import com.testing.vladyslav.cubes.util.UserModelHelper;

import java.util.ArrayList;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class FigureBuilder {

    public class FigureParameters{

        private final int dimensionNumber = 9;
        private float [] modelDimensions = new float [dimensionNumber];

        private int maxModelSize = Settings.maximumModelSize;

        private final int sizeX = 0;
        private final int sizeY = 1;
        private final int sizeZ = 2;

        private final int minX = 3;
        private final int maxX = 4;

        private final int minZ = 5;
        private final int maxZ = 6;

        private final int minY = 7;
        private final int maxY = 8;


        int cubeNumber = 0;

        void setSizeX(int x){ modelDimensions[sizeX] = x;}
        void setSizeY(int y){ modelDimensions[sizeY] = y;}
        void setSizeZ(int z){ modelDimensions[sizeZ] = z;}


        void clearFigureDimensions(){
            modelDimensions = new float[dimensionNumber];
        }

        float getFigureMaxXZDimen(){

            return Math.max(
                    Math.max(Math.abs(modelDimensions[maxX]), Math.abs(modelDimensions[minX])),
                    Math.max(Math.abs(modelDimensions[maxZ]), Math.abs(modelDimensions[minZ])));

        }

        public float getFigureMaxXYZDimen(){

            return Math.max(gridBuilder.gridSize, maxY - minY);

        }

        boolean cubeOutOfBounds(PixioPoint center){
            return (center.y - modelDimensions[minY] >= maxModelSize ||
                            Math.abs(center.x)> gridBuilder.maxGridSize/2 ||
                            Math.abs(center.z)>gridBuilder.maxGridSize /2);

        }

        public UserModel getModel(){

            UserModel model = new UserModel();
            model.setCubeNumber(cubeNumber);
            model.setSizeY(sizeY);
            model.setSizeX(sizeX);
            model.setSizeZ(sizeZ);
            model.setCubes(UserModelHelper.getStringModelForm(cubes));

            return model;

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

    }

    private static final String TAG = "FigureBuilder";

    private float cubeSize = 1f;
    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int NORMAL_COMPONENT_COUNT = 3;
    private int STRIDE = 0;


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


    private ArrayList<Cube> cubes;
    private ArrayList<PixioPoint> cubeCenters;

    private GridBuilder gridBuilder;
    private FigureChangesManager changesManager;
    private FigureParameters params;

    public ArrayList<PixioPoint> getCubeCenters(){ return cubeCenters;}

    public void setGridBuilder(GridBuilder grid){this.gridBuilder = grid;}


    public FigureBuilder(){

        cubes = new ArrayList<>();
        cubeCenters = new ArrayList<>();

        params = new FigureParameters();
        changesManager = new FigureChangesManager(new FigureChangesManager.Action() {
            @Override
            public void addCube(PixioPoint center, int color) {
                FigureBuilder.this.addNewCube(center, color);
            }

            @Override
            public void deleteCube(PixioPoint center) {
                FigureBuilder.this.deleteCube(center);
            }

            @Override
            public void paintCube(PixioPoint center, int color) {
                FigureBuilder.this.changeCubeColor(center, color);
            }
        });

    }

    public FigureChangesManager getChangesManager(){return changesManager;}

    public FigureParameters getFigureParams(){return params;}

    public void setModel(UserModel model){

        params.setSizeX(model.getSizeX() == null? 0: model.getSizeX());
        params.setSizeY(model.getSizeY() == null? 0: model.getSizeY());
        params.setSizeZ(model.getSizeZ() == null? 0: model.getSizeZ());

        params.cubeNumber = model.getCubeNumber() == null? 0: model.getCubeNumber();

        this.cubes = UserModelHelper.getCubesModelForm(model.getCubes() == null? "" : model.getCubes());
        this.cubeCenters = new ArrayList<>();

        for (Cube cube: cubes){
            cubeCenters.add(cube.center);
        }

        build(cubes);

        bindAttributesData();

    }

    public void addNewCubeClicked(PixioPoint center, int colorIndex){

        if(center == null) return;
        if(cubeExists(center)) return;
        if(params.cubeOutOfBounds(center) && !Settings.unlimitedGridSize) return;


        changesManager.newCommandAddCube(center, colorIndex);

    }

    public void deleteCubeClicked(PixioPoint center){

        if(center == null) return;
        if(!cubeExists(center)) return;

        changesManager.newCommandDeleteCube(center, PixioHelper.hexToColorCode.get(getCubeByCenter(center).color.hexColor));

    }

    public void paintCubeClicked(PixioPoint center, int colorIndex){

        if(center == null) return;
        if(!cubeExists(center)) return;

        changesManager.newCommandPaintCube(center,  PixioHelper.hexToColorCode.get(getCubeByCenter(center).color.hexColor), colorIndex);

    }

    public void forwardClicked(){

        changesManager.forward();

    }

    public void backwardClicked(){

        changesManager.backward();

    }

    public void build(){
        build(cubes);
    }

    private void build(ArrayList<Cube> cubes){

        if(params.cubeNumber<=0) return;

        vertexColorDataOffset = 0;
        vertexDataOffset = 0;
        vertexNormalDataOffset = 0;

        vertexPositionData = new float[CubeDataHolder.getInstance().sizeInVertex * POSITION_COMPONENT_COUNT * params.cubeNumber];
        vertexNormalData = new float[CubeDataHolder.getInstance().sizeInVertex * NORMAL_COMPONENT_COUNT * params.cubeNumber];
        vertexColorData = new float[(vertexPositionData.length / POSITION_COMPONENT_COUNT) * COLOR_COORDINATES_COMPONENT_COUNT];

        params.clearFigureDimensions();

        for (Cube cube: cubes){

            cube.createCubeData();
            appendCube(cube);
            cube.releaseCubeData();

            params.updateFigureDimensions(cube);

        }

        gridBuilder.setGridSize(Math.round(params.getFigureMaxXZDimen()+1) *2);

        vertexPosArray = new VertexArray(vertexPositionData);
        vertexColorArray = new VertexArray(vertexColorData);
        vertexNormalArray = new VertexArray(vertexNormalData);

        vertexPositionData = null;
        vertexNormalData = null;
        vertexColorData = null;

    }


    private void addNewCube(PixioPoint center, final int colorIndex){

        //check if this cube already exists
        if(cubeExists(center)) return;

        params.cubeNumber++;
        cubeCenters.add(center);
        cubes.add(new Cube(center, new PixioColor(PixioHelper.colorCodeToHex.get(colorIndex))));

        //rebuild figure
        build(cubes);
        bindAttributesData();

    }

    private void deleteCube(PixioPoint center){

        if(!cubeExists(center)) return;

        params.cubeNumber--;
        cubes.remove(getCubeByCenter(center));
        cubeCenters.remove(center);

        //rebuild figure
        build(cubes);
        bindAttributesData();


    }

    private void changeCubeColor(PixioPoint center, int colorIndex){

        for (Cube cube : cubes) {

            if (cube.center.equals(center)) {

                cube.color = new PixioColor(PixioHelper.colorCodeToHex.get(colorIndex));

                build(cubes);

                bindAttributesData();

                break;

            }

        }

    }

    private Cube getCubeByCenter(PixioPoint center){

        for (Cube cube: cubes){
            if (cube.center.equals(center)){
                return cube;
            }
        }

        return new Cube(center, new PixioColor("#ffffff"));

    }

    private boolean cubeExists(PixioPoint cubeCenter){
        for (PixioPoint oldCenter: cubeCenters){
            if (cubeCenter.equals(oldCenter)){
                return true;
            }
        }
        return false;
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


        if(params.cubeNumber <=0){
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

        if(params.cubeNumber <= 0){
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


        glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeDataHolder.getInstance().sizeInVertex * params.cubeNumber);

    }


}

package com.testing.vladyslav.cubes.objects;

import android.opengl.GLES20;
import android.util.Log;

import com.testing.vladyslav.cubes.Settings;
import com.testing.vladyslav.cubes.animation.Animator;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.data.VertexArray;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.objects.userActionsManagement.FigureChangesManager;
import com.testing.vladyslav.cubes.shaders.ModelShader;
import com.testing.vladyslav.cubes.util.Geometry;
import com.testing.vladyslav.cubes.util.PixioColor;
import com.testing.vladyslav.cubes.util.PixioHelper;
import com.testing.vladyslav.cubes.util.UserModelHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glDeleteBuffers;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES20.glVertexAttribPointer;

public class FigureBuilder {

    public class FigureParameters{

        private final int dimensionNumber = 13;
        private float [] modelDimensions = new float [dimensionNumber];

        private int maxModelSize = Settings.maximumModelSize;
        private String name = "";

        private final int sizeX = 0;
        private final int sizeY = 1;
        private final int sizeZ = 2;

        private final int minX = 3;
        private final int maxX = 4;

        private final int minZ = 5;
        private final int maxZ = 6;

        private final int minY = 7;
        private final int maxY = 8;

        private final int boundMinX = 9;
        private final int boundMaxX = 10;

        private final int boundMinZ = 11;
        private final int boundMaxZ = 12;

        int cubeNumber = 0;

        FigureParameters(){

            clearFigureDimensions();
            modelDimensions[boundMinX] = - Settings.maximumGridSize / 2;
            modelDimensions[boundMaxX] = Settings.maximumGridSize / 2;
            modelDimensions[boundMinZ] = - Settings.maximumGridSize / 2;
            modelDimensions[boundMaxZ] = Settings.maximumGridSize / 2;
        }

        void setSizeX(int x){ modelDimensions[sizeX] = x;}
        void setSizeY(int y){ modelDimensions[sizeY] = y;}
        void setSizeZ(int z){ modelDimensions[sizeZ] = z;}
        void setName(String name){this.name = name;}


        void clearFigureDimensions(){
            for (int i = 0; i< 3; i++){
                modelDimensions[i] = 0;
            }
            modelDimensions[minX] = 0;
            modelDimensions[maxX] = 0;
            modelDimensions[minY] = 0;
            modelDimensions[maxY] = 0;
            modelDimensions[minZ] = 0;
            modelDimensions[maxZ] = 0;

        }

        float getFigureMaxXZDimen(){

            return Math.max(
                    Math.max(Math.abs(modelDimensions[maxX]), Math.abs(modelDimensions[minX])),
                    Math.max(Math.abs(modelDimensions[maxZ]), Math.abs(modelDimensions[minZ])));

        }

        public float getFigureMaxXYZDimen(){

            return Math.max(gridBuilder.gridSize, maxY - minY);

        }

        public float getYSize(){return modelDimensions[sizeY];}

        boolean cubeOnTheBound(PixioPoint center){
            return (center.x == modelDimensions[boundMinX] + Settings.cubeSize/2 ||
                    center.x == modelDimensions[boundMaxX] - Settings.cubeSize/2 ||
                    center.z == modelDimensions[boundMinZ] + Settings.cubeSize/2 ||
                    center.z == modelDimensions[boundMaxZ] - Settings.cubeSize/2);
        }

        Geometry.Vector countStride(PixioPoint center){

            float strideX = 0f;
            float strideY = 0f;
            float strideZ = 0f;

            if(center.x == modelDimensions[boundMinX] + Settings.cubeSize/2 && modelDimensions[sizeX] < Settings.maximumGridSize-1){
                strideX = 1f;
            }
            if(center.x == modelDimensions[boundMaxX] - Settings.cubeSize/2 && modelDimensions[sizeX] < Settings.maximumGridSize-1){
                strideX = -1f;
            }
            if(center.z == modelDimensions[boundMinZ] + Settings.cubeSize/2 && modelDimensions[sizeZ] < Settings.maximumGridSize-1){
                strideZ = 1f;
            }
            if(center.z == modelDimensions[boundMaxZ] - Settings.cubeSize/2 && modelDimensions[sizeZ] < Settings.maximumGridSize-1){
                strideZ = -1f;
            }

            if(strideX == 0 && strideY == 0 && strideZ == 0){
                return null;
            }else{
                return new Geometry.Vector(strideX, strideY, strideZ);
            }

        }

        boolean cubeOutOfBounds(PixioPoint center){
            return (center.y - modelDimensions[minY] >= Settings.maximumGridSize ||
                            Math.abs(center.x)> gridBuilder.maxGridSize/2 ||
                            Math.abs(center.z)>gridBuilder.maxGridSize /2);

        }

        public UserModel getModel(){

            UserModel model = new UserModel();
            model.setName(name);
            model.setCubeNumber(cubeNumber);
            model.setSizeY(sizeY);
            model.setSizeX(sizeX);
            model.setSizeZ(sizeZ);
            model.setCubes(UserModelHelper.getStringModelForm(cubes));

            return model;

        }

        public ArrayList<Cube> getCubeList(){
            return cubes;
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
            if(cube.center.x > modelDimensions[maxX] || modelDimensions[maxX] == 0){
                modelDimensions[maxX] = cube.center.x + Settings.cubeSize/2;
            }

            if(cube.center.x < modelDimensions[minX] || modelDimensions[minX] == 0){
                modelDimensions[minX] = cube.center.x - Settings.cubeSize/2;
            }

            if(cube.center.y > modelDimensions[maxY] || modelDimensions[maxY] == 0){
                modelDimensions[maxY] = cube.center.y + Settings.cubeSize/2;
            }

            if(cube.center.y < modelDimensions[minY] || modelDimensions[minY] == 0){
                modelDimensions[minY] = cube.center.y - Settings.cubeSize/2;
            }

            if(cube.center.z > modelDimensions[maxZ] || modelDimensions[maxZ] == 0){
                modelDimensions[maxZ] = cube.center.z + Settings.cubeSize/2;
            }

            if(cube.center.z < modelDimensions[minZ] || modelDimensions[minZ] == 0){
                modelDimensions[minZ] = cube.center.z - Settings.cubeSize/2;
            }

            modelDimensions[sizeX] = modelDimensions[maxX] - modelDimensions[minX];
            modelDimensions[sizeZ] = modelDimensions[maxZ] - modelDimensions[minZ];
            modelDimensions[sizeY] = modelDimensions[maxY] - modelDimensions[minY];

        }

    }

    private static final String TAG = "FigureBuilder";

    private float cubeSize = 1f;
    private int POSITION_COMPONENT_COUNT = 3;
    private int COLOR_COORDINATES_COMPONENT_COUNT = 4;
    private int NORMAL_COMPONENT_COUNT = 3;
    private int STRIDE = 0;
    private String tag = "FigureBuilder";


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
    private Animator animator;

    private boolean viewMode = false;
    private boolean isScattered = false;

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

            @Override
            public void strideFigure(Geometry.Vector strideVector) {
                FigureBuilder.this.strideFigure(strideVector);
            }
        });

    }

    public FigureChangesManager getChangesManager(){return changesManager;}

    public FigureParameters getFigureParams(){return params;}

    public void setModel(UserModel model){

        params.setSizeX(model.getSizeX() == null? 0: model.getSizeX());
        params.setSizeY(model.getSizeY() == null? 0: model.getSizeY());
        params.setSizeZ(model.getSizeZ() == null? 0: model.getSizeZ());
        params.setName(model.getName());

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

        Geometry.Vector strideVector = null;

        if(center == null) return;
        if(cubeExists(center)) return;
        if(!Settings.unlimitedGrid){
            if(params.cubeOutOfBounds(center)) return;
            if(params.cubeOnTheBound(center)){
                strideVector = params.countStride(center);
            }
        }

        changesManager.newCommandAddCube(center, colorIndex, strideVector);

    }

    public void deleteCubeClicked(PixioPoint center){

        if(center == null) return;
        if(!cubeExists(center)) return;

        try{
            changesManager.newCommandDeleteCube(center, PixioHelper.hexToColorCode.get(getCubeByCenter(center).color.hexColor), null);
        }catch (Exception e){
            Log.d(tag, getCubeByCenter(center).color.hexColor);
            e.printStackTrace();
        }


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

    private void strideFigure(Geometry.Vector strideVector){

        for (Cube cube: cubes){
            cube.center.translate(strideVector);
        }

        build(cubes);
        bindAttributesData();

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

    public void openFigure(){
        isScattered = !isScattered;
    }

    public void setViewMode(boolean b){
        viewMode = b;
        if(viewMode){
            Collections.sort(cubes);
            animator = new Animator(params.cubeNumber, Math.round(params.getYSize()), cubes);
        }else{
            isScattered = false;
        }

    }



    public void draw(ModelShader shader){

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


        if(viewMode && animator!=null){

            if (isScattered){

                animator.drawOpenedFigure(shader);

            }else{

                animator.drawClosedFigure(shader);

            }

        }else{

            float[] resetScatterVector = {0.0f, 0.0f, 0.0f};
            shader.setScatter(resetScatterVector);
            //animator.drawClosedFigure(shader);
            glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeDataHolder.getInstance().sizeInVertex * params.cubeNumber);
        }


    }


}

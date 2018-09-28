package com.testing.vladyslav.cubes;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.objects.Background;
import com.testing.vladyslav.cubes.objects.Cube;
import com.testing.vladyslav.cubes.objects.GridBuilder;
import com.testing.vladyslav.cubes.objects.FigureBuilder;
import com.testing.vladyslav.cubes.objects.PixioPoint;
import com.testing.vladyslav.cubes.objects.userActionsManagement.FigureChangesManager;
import com.testing.vladyslav.cubes.shaders.BackGroundShader;
import com.testing.vladyslav.cubes.shaders.DynamicModelShader;
import com.testing.vladyslav.cubes.shaders.GridShaderProgram;
import com.testing.vladyslav.cubes.shaders.ModelShader;
import com.testing.vladyslav.cubes.shaders.StaticModelShader;
import com.testing.vladyslav.cubes.util.ObjectSelectHelper;
import com.testing.vladyslav.cubes.util.PixioColor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glReadPixels;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.translateM;

public class CubeRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private volatile float xAngle = -45f;
    private volatile float yAngle = 10f;

    private float strideX = 0f;
    private float strideY = 0f;



    private float screenshotXAngle = -45f;
    private float screenshotYAngle = 10f;

    private static final String TAG = "CubeRenderer";


    //used to move and rotate objects around the word
    private float[] modelMatrix = new float[16];
    //camera matrix - positions objects relative to our eyes
    private float[] viewMatrix = new float[16];
    //project the world into 3d
    private float[] projectionMatrix = new float[16];

    private float[] viewProjectionMatrix = new float[16];

    float [] MVPMatrix = new float[16];

    //matrix that undo the effects of view and projection matrix
    private final float[] invertedViewProjectionMatrix = new float[16];

    /**
     * Stores a copy of the model matrix specifically for the light position.
     */
    private final float[] lightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};

    private float[] frontLightModelMatrix = new float[16];
    private final float[] frontLightPosInWorldSpace = new float[4];
    private final float[] frontLightPosInEyeSpace = new float[4];

    private float[] backLightModelMatrix = new float[16];
    private final float[] backLightPosInWorldSpace = new float[4];
    private final float[] backLightPosInEyeSpace = new float[4];

    private float[] leftLightModelMatrix = new float[16];
    private final float[] leftLightPosInWorldSpace = new float[4];
    private final float[] leftLightPosInEyeSpace = new float[4];

    private float[] rightLightModelMatrix = new float[16];
    private final float[] rightLightPosInWorldSpace = new float[4];
    private final float[] rightLightPosInEyeSpace = new float[4];

    private float[] topLightModelMatrix = new float[16];
    private final float[] topLightPosInWorldSpace = new float[4];
    private final float[] topLightPosInEyeSpace = new float[4];

    private float[] bottomLightModelMatrix = new float[16];
    private final float[] bottomLightPosInWorldSpace = new float[4];
    private final float[] bottomLightPosInEyeSpace = new float[4];

    //shaders
    private GridShaderProgram gridShader;
    private ModelShader cubeShader;
    private BackGroundShader backGroundShader;

    //objects on the screen
    private FigureBuilder builder;
    private GridBuilder gridBuilder;
    private Background background;

    private boolean lowerGrid = false;

    private boolean shouldAddCube = false;
    private boolean shouldEditCubeColor = false;
    private boolean shouldDeleteCube = false;
    private boolean shouldBackwards = false;
    private boolean shouldForward = false;
    private boolean shouldLoadNewModel = false;
    private boolean shouldBindAttributesData = false;

    private float scaleFactor = 1f;
    private ObjectSelectHelper.TouchResult touchResult;
    private UserModel renderingModel;

    private int currentColorIndex = 240;

    private boolean buildingMode = false;
    private boolean colorEditingMode = false;
    private boolean deleteMode = false;
    private boolean screenshotMode = false;
    private boolean viewMode = false;

    private int width;
    private int height;

    private RendererState currentState;
    private ScreenshotHandler screenshotHandler;
    private ChangesRequestedListener changesRequestedListener;

    public interface ChangesRequestedListener{
        void onActionRequested();
    }

    public interface ScreenshotHandler{
        void makeScreenshot(Bitmap bitmap);
    }

    private void changeRequested(){
        if(changesRequestedListener!=null){
            changesRequestedListener.onActionRequested();
        }
    }

    public void backward(){
        shouldBackwards = true;
        changeRequested();
    }

    public void forward(){
        shouldForward = true;
        changeRequested();
    }


    public void setChangesRequestListener(ChangesRequestedListener listener){
        this.changesRequestedListener = listener;
    }

    public FigureChangesManager getFigureChangeManager(){return builder.getChangesManager();}

    public void resetModes(){buildingMode = false; colorEditingMode = false; deleteMode = false;}

    public void setScreenshotMode(ScreenshotHandler screenshotHandler){
        this.screenshotMode = true;
        this.screenshotHandler = screenshotHandler;
        currentState = new MakeScreenshotState(currentState);
    }

    public void setViewMode(boolean b){
        viewMode = b;
        strideY = 0f;
        strideX = 0f;

        shouldAddCube = false;
        shouldEditCubeColor = false;
        shouldDeleteCube = false;

        builder.setViewMode(viewMode);

        if(viewMode){
            shouldBindAttributesData = true;
            currentState = new ViewState(currentState);
        }else{

            shouldBindAttributesData = true;
            currentState.returnPreviousState();
            //centerFigureOnScreen();

        }
    }

    public void setBuildingMode(){resetModes(); buildingMode = true; }

    public void setColorEditingMode(){resetModes(); colorEditingMode = true;}

    public void setDeleteMode(){resetModes(); deleteMode = true;}

    public void setScaleFactor(float scaleFactor) {this.scaleFactor = scaleFactor; }

    public float getScaleFactor() {return scaleFactor;}

    public void setColor(int colorIndex){this.currentColorIndex = colorIndex;}

    public void setStride(float strideX, float strideY){ if(viewMode) return; this.strideX = strideX; this.strideY = strideY; }
    public void setXAngle(float xAngle) { this.xAngle = xAngle; }
    public void setYAngle(float yAngle) { this.yAngle = yAngle; }

    public float getStrideX(){return strideX;}
    public float getStrideY(){return strideY;}
    public float getXAngle() {return this.xAngle; }
    public float getYAngle() { return this.yAngle; }

    public CubeRenderer(Context context) {

        this.context = context;
        builder = new FigureBuilder();
        currentState = new EditingState();

    }

    private void lowerGrid(){
        lowerGrid = true;
    }

    public void handleTouch(float normalizedX, float normalizedY){

        if(viewMode){

            builder.openFigure();
            return;

        }

        ArrayList<PixioPoint> cubeCenters = new ArrayList<>(builder.getCubeCenters());
        ArrayList<PixioPoint> tileCenters = new ArrayList<>(gridBuilder.getTileCenters());

        cubeCenters.addAll(tileCenters);
        touchResult = ObjectSelectHelper.getTouchResult(cubeCenters, normalizedX, normalizedY, invertedViewProjectionMatrix, modelMatrix, scaleFactor, strideX, strideY, (float)height/width, Settings.gridHeight);
        if(!touchResult.cubeTouched) return;

        if(buildingMode){

            shouldAddCube = true;

        }

        if(colorEditingMode){

            shouldEditCubeColor = true;

        }

        if(deleteMode){

            shouldDeleteCube = true;

        }

        changeRequested();

    }

    public UserModel getRenderingModel(){

        return builder.getFigureParams().getModel();

    }

    public ArrayList<Cube> getCubes(){
        return builder.getFigureParams().getCubeList();
    }

    public void setRenderingModel(UserModel model){

        this.renderingModel = model;
        shouldLoadNewModel = true;


    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {


        PixioColor bgColor = new PixioColor(Settings.editorBackGroundColor);
        glClearColor(bgColor.RED, bgColor.GREEN, bgColor.BLUE, 0.0f);
        //GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

        // Use culling to remove back faces.
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        // Enable depth testing to remove drawing objects that are behind other objects
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -0.5f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = -5.0f;

        // Set our up vector. This is where our head would be pointing were we holding the camera.
        final float upX = 0.0f;
        final float upY = 1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
        Matrix.setLookAtM(viewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);

        Matrix.translateM(modelMatrix, 0, 0f, 0f, 7.0f);


        gridShader = new GridShaderProgram(context);

        gridBuilder = new GridBuilder();
        gridBuilder.build();

        builder.setGridBuilder(gridBuilder);
        cubeShader = Settings.dynamicShadows? new DynamicModelShader(context): new StaticModelShader(context);

        builder.build();


        gridBuilder.bindAttributesData();
        builder.bindAttributesData();

        background = new Background();
        background.bindAttributesData();
        backGroundShader = new BackGroundShader(context);


    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to the same size as the surface.
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 3.0f;
        final float far = 100.0f;

        this.width = width;
        this.height = height;

        Matrix.orthoM(projectionMatrix, 0, left,right, bottom, top, near, far);

    }

    @Override
    public void onDrawFrame(GL10 glUnused) {

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if(currentState != null){ currentState.draw();}

    }

    private abstract class RendererState{

        RendererState previousState;

        RendererState(){};

        public void draw(){ }

        void calculateModelMatrix(){

            //manipulations with the cubes model matrix
            //push figure to the distance
            Matrix.setIdentityM(modelMatrix, 0);
            Matrix.translateM(modelMatrix, 0, 0f, 0f, -7.0f);

        }

        void calculateLightMatrices(float xAngle, float yAngle){

            Matrix.setIdentityM(frontLightModelMatrix, 0);
            Matrix.translateM(frontLightModelMatrix, 0, 0.0f, 0.0f, -7.0f);

            //count all the light source position
            float lightDistance = Settings.lightDistance;
            if(Settings.dynamicShadows){
                Matrix.translateM(frontLightModelMatrix, 0, 0.0f, 0.0f, lightDistance);
                multiplyMV(frontLightPosInWorldSpace, 0, frontLightModelMatrix, 0, lightPosInModelSpace, 0);
                multiplyMV(frontLightPosInEyeSpace, 0, viewMatrix, 0, frontLightPosInWorldSpace, 0);
            }else{
                rotateM(frontLightModelMatrix, 0, yAngle, 1f, 0f, 0f);
                rotateM(frontLightModelMatrix, 0, xAngle, 0f, 1f, 0f);

                System.arraycopy(frontLightModelMatrix ,0, backLightModelMatrix, 0, frontLightModelMatrix.length);
                System.arraycopy(frontLightModelMatrix ,0, rightLightModelMatrix, 0, frontLightModelMatrix.length);
                System.arraycopy(frontLightModelMatrix ,0, topLightModelMatrix, 0, frontLightModelMatrix.length);
                //System.arraycopy(frontLightModelMatrix ,0, leftLightModelMatrix, 0, frontLightModelMatrix.length);
                //System.arraycopy(frontLightModelMatrix ,0, bottomLightModelMatrix, 0, frontLightModelMatrix.length);

                Matrix.translateM(backLightModelMatrix, 0, 0.0f, 0.0f, -lightDistance);
                Matrix.translateM(rightLightModelMatrix, 0, lightDistance, 0.0f, 0.0f);
                Matrix.translateM(topLightModelMatrix,  0, 0.0f, lightDistance, 0.0f);
                //Matrix.translateM(leftLightModelMatrix, 0, -lightDistance, 0.0f, 0.0f);
                //Matrix.translateM(bottomLightModelMatrix, 0, 0.0f, -lightDistance, 0.0f);



                multiplyMV(frontLightPosInWorldSpace, 0, frontLightModelMatrix, 0, lightPosInModelSpace, 0);
                multiplyMV(frontLightPosInEyeSpace, 0, viewMatrix, 0, frontLightPosInWorldSpace, 0);

                multiplyMV(backLightPosInWorldSpace, 0, backLightModelMatrix, 0, lightPosInModelSpace, 0);
                multiplyMV(backLightPosInEyeSpace, 0, viewMatrix, 0, backLightPosInWorldSpace, 0);

                multiplyMV(rightLightPosInWorldSpace, 0, rightLightModelMatrix, 0, lightPosInModelSpace, 0);
                multiplyMV(rightLightPosInEyeSpace, 0, viewMatrix, 0, rightLightPosInWorldSpace, 0);

                multiplyMV(topLightPosInWorldSpace, 0, topLightModelMatrix, 0, lightPosInModelSpace, 0);
                multiplyMV(topLightPosInEyeSpace, 0, viewMatrix, 0, topLightPosInWorldSpace, 0);

                //Matrix.multiplyMV(leftLightPosInWorldSpace, 0, leftLightModelMatrix, 0, lightPosInModelSpace, 0);
                //Matrix.multiplyMV(leftLightPosInEyeSpace, 0, viewMatrix, 0, leftLightPosInWorldSpace, 0);

                //Matrix.multiplyMV(bottomLightPosInWorldSpace, 0, bottomLightModelMatrix, 0, lightPosInModelSpace, 0);
                //Matrix.multiplyMV(bottomLightPosInEyeSpace, 0, viewMatrix, 0, bottomLightPosInWorldSpace, 0);


            }

        }

        void setUniforms(){
            // Set our per-vertex lighting gridShader.
            cubeShader.useProgram();
            if(Settings.dynamicShadows){
                cubeShader.setUniforms(modelMatrix, viewMatrix, projectionMatrix,
                        frontLightPosInEyeSpace, null, null, null, null, null);
            }else{
                cubeShader.setUniforms(modelMatrix, viewMatrix, projectionMatrix,
                        frontLightPosInEyeSpace,
                        backLightPosInEyeSpace,
                        leftLightPosInEyeSpace,
                        rightLightPosInEyeSpace,
                        topLightPosInEyeSpace,
                        bottomLightPosInEyeSpace);
            }
        }

        void calculateMVPMatrix(){

            multiplyMM(viewProjectionMatrix, 0, viewMatrix, 0, projectionMatrix, 0);
            invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
            translateM(invertedViewProjectionMatrix, 0, 0f, 0f, -10f);

            MVPMatrix = getMVPMatrix(modelMatrix, viewMatrix, projectionMatrix);

        }

        void drawBackground(){
            backGroundShader.useProgram();
            background.draw(backGroundShader);
        }

        void setScaleFactor(){
            cubeShader.setScaleFactor(scaleFactor);
        }

        void drawGrid(){
            //draw the grid
            gridShader.useProgram();
            gridShader.setUniforms(modelMatrix, viewMatrix, projectionMatrix);
            gridShader.setScaleFactor(scaleFactor);
            gridBuilder.draw(gridShader);
        }

        void drawFigure(){
            builder.draw(cubeShader);
        }

        void makeScreenshot(){
            if(screenshotHandler != null){

                screenshotHandler.makeScreenshot(getScreenshot());

            }
            screenshotMode = false;
        }

        void returnPreviousState(){
            currentState = previousState;
        }

    }

    private class EditingState extends RendererState{


        @Override
        public void draw() {

            super.draw();

            if(shouldAddCube){

                builder.addNewCubeClicked(touchResult.newCubeCenter, currentColorIndex);
                shouldAddCube = false;

            }

            if(shouldEditCubeColor){

                builder.paintCubeClicked(touchResult.touchedCubeCenter, currentColorIndex);
                shouldEditCubeColor = false;

            }

            if(shouldDeleteCube){

                builder.deleteCubeClicked(touchResult.touchedCubeCenter);
                shouldDeleteCube = false;

            }

            if(shouldBackwards){
                builder.backwardClicked();
                shouldBackwards = false;
            }

            if(shouldForward){
                builder.forwardClicked();
                shouldForward = false;
            }

            if(shouldBindAttributesData){
                builder.build();
                builder.bindAttributesData();
                shouldBindAttributesData = false;
            }

            if(shouldLoadNewModel){
                builder.setModel(renderingModel);
                //fit figure on screen
                float figureSize = builder.getFigureParams().getFigureMaxXYZDimen();

                if(figureSize>8){
                    scaleFactor = 8/figureSize;
                    cubeShader.setScaleFactor(scaleFactor);

                }

                centerFigureOnScreen();

                shouldLoadNewModel = false;
            }

            calculateModelMatrix();

            drawGrid();

            calculateLightMatrices(xAngle, yAngle);

            calculateMVPMatrix();

            setUniforms();

            setScaleFactor();

            drawFigure();

        }

        @Override
        protected void calculateModelMatrix() {

            centerFigureOnScreen();

            super.calculateModelMatrix();
            //set user made stride
            Matrix.translateM(modelMatrix, 0, strideX/scaleFactor, -strideY/scaleFactor, 0.0f);
            //set user made rotation

            rotateM(modelMatrix, 0, yAngle, 1f, 0f, 0f);
            rotateM(modelMatrix, 0, xAngle, 0f, 1f, 0f);
        }



    }

    private class ViewState extends RendererState{

        ViewState(RendererState previousState){
            this.previousState = previousState;
        }


        @Override
        public void draw() {
            super.draw();

            if(shouldBindAttributesData){
                builder.build();
                builder.bindAttributesData();
                shouldBindAttributesData = false;
            }

            calculateModelMatrix();

            calculateLightMatrices(xAngle, yAngle);

            setUniforms();

            setScaleFactor();

            drawFigure();


        }

        @Override
        protected void calculateModelMatrix() {

            super.calculateModelMatrix();

            Matrix.translateM(modelMatrix, 0, strideX/scaleFactor, -strideY/scaleFactor, 0.0f);

            //set user made rotation
            rotateM(modelMatrix, 0, yAngle, 1f, 0f, 0f);
            rotateM(modelMatrix, 0, xAngle, 0f, 1f, 0f);
        }


    }

    private void centerFigureOnScreen(){

        PixioPoint figureCenter = builder.getFigureParams().getFigureCenter();
        strideX = -figureCenter.x * scaleFactor;
        strideY = figureCenter.y * scaleFactor;

    }

    private class MakeScreenshotState extends RendererState{

        MakeScreenshotState(RendererState previousState){
            this.previousState = previousState;
        }

        @Override
        public void draw() {

            drawBackground();

            calculateModelMatrix();

            calculateLightMatrices(screenshotXAngle, screenshotYAngle);

            setUniforms();

            setScaleFactor();

            drawFigure();

            makeScreenshot();

            returnPreviousState();

        }

        @Override
        void calculateModelMatrix() {
            super.calculateModelMatrix();

            PixioPoint figureCenter = builder.getFigureParams().getFigureCenter();
            Matrix.translateM(modelMatrix, 0, -figureCenter.x, -figureCenter.y, -figureCenter.z);

            rotateM(modelMatrix, 0, screenshotYAngle, 1f, 0f, 0f);
            rotateM(modelMatrix, 0, screenshotXAngle, 0f, 1f, 0f);

        }

        @Override
        void setScaleFactor() {

            float figureSize = builder.getFigureParams().getFigureMaxXYZDimen();

            if(figureSize>8){
                float scale = 8/figureSize;
                cubeShader.setScaleFactor(scale);
            }

        }

    }

    private float[] getMVPMatrix(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix){

        float [] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        return MVPMatrix;

    }

    private Bitmap getScreenshot(){
        int screenshotSize = width * height;
        ByteBuffer bb = ByteBuffer.allocateDirect(screenshotSize * 4);
        bb.order(ByteOrder.nativeOrder());
        glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, bb);
        int pixelsBuffer[] = new int[screenshotSize];
        bb.asIntBuffer().get(pixelsBuffer);
        bb = null;

        for (int i = 0; i < screenshotSize; ++i) {
            // The alpha and green channels' positions are preserved while the      red and blue are swapped
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) |    ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenshotSize-width, -width, 0, 0, width, height);


        return bitmap;
    }




}

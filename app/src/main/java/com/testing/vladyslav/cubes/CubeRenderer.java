package com.testing.vladyslav.cubes;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.testing.vladyslav.cubes.objects.GridBuilder;
import com.testing.vladyslav.cubes.objects.FigureBuilder;
import com.testing.vladyslav.cubes.objects.Point;
import com.testing.vladyslav.cubes.programs.GridShaderProgram;
import com.testing.vladyslav.cubes.programs.ShaderProgram;
import com.testing.vladyslav.cubes.util.Geometry;
import com.testing.vladyslav.cubes.util.Geometry.*;

import java.util.ArrayList;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.Matrix.invertM;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.multiplyMV;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.translateM;
import static com.testing.vladyslav.cubes.util.ObjectSelectHelper.convertNormalized2DPointToRay;
import static com.testing.vladyslav.cubes.util.ObjectSelectHelper.getTouchedCubeSide;

public class CubeRenderer implements GLSurfaceView.Renderer {
    private Context context;

    private volatile float xAngle = 0f;
    private volatile float yAngle = 0f;

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

    private float[] scatter = {0.0f, 2.0f, 0.0f};

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
    private ShaderProgram cubeShader;

    //objects on the screen
    private FigureBuilder builder;
    private GridBuilder gridBuilder;

    private boolean lowerGrid = false;
    private float gridHeight = 0f;

    private boolean shouldAddCube = false;
    private Point newCubeCenter;

    private float scaleFactor = 1f;

    private boolean cubePressed = false;
    private Point cubePosition;


    //connection with the main activity
    private CubeRendererListener listener;

    public interface CubeRendererListener{
        void onTouched(String txt);
    }

    public void setListener (CubeRendererListener listener){ this.listener = listener; }




    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }



    public float getXAngle() {
        return this.xAngle;
    }

    public void setXAngle(float xAngle) {
        this.xAngle = xAngle;
    }

    public float getYAngle() {
        return this.yAngle;
    }

    public void setYAngle(float yAngle) {
        this.yAngle = yAngle;

    }

    public CubeRenderer(Context context) {
        this.context = context;
    }

    private void lowerGrid(){
        lowerGrid = true;
    }


    public void handleTouchPress(float normalizedX, float normalizedY){
        //if(builder!=null){builder.handleTouchPress();}
        lowerGrid();

        addCube(normalizedX, normalizedY);






    }

    private void addCube(float normalizedX, float normalizedY){

        ArrayList<Point> cubeCenters = new ArrayList<>(builder.getCubeCenters());
        float cubeSize = 1f;
        float sphereRadius = cubeSize / 2 * (float)Math.sqrt(2);

        Geometry.Ray ray = convertNormalized2DPointToRay(normalizedX * 10, normalizedY * 10, invertedViewProjectionMatrix);

        Iterator<Point> iterator = cubeCenters.iterator();

        while(iterator.hasNext()){

            float[] cubePos = new float[4];

            Point cubeCenter = iterator.next();
            multiplyMV (cubePos,0, modelMatrix, 0, new float[]{cubeCenter.x, cubeCenter.y, cubeCenter.z, 0}, 0);
            Geometry.Sphere cubeBoundingSphere = new Geometry.Sphere(new Point (cubePos[0], cubePos[1], cubePos[2]), sphereRadius);

            boolean intersects = Geometry.intersects(cubeBoundingSphere, ray);

            if(!intersects){

                iterator.remove();

            }
            else {

                Point newCenter = getTouchedCubeSide(cubeCenter, ray.point, modelMatrix);
                if (newCenter == null){

                    iterator.remove();

                }

            }


        }



        if(cubeCenters.size() > 0){

            Point closestPoint = cubeCenters.get(0);

            float closestSpot = -1000f;

            for (Point cubeCenter: cubeCenters){

                if(cubeCenter.z > closestSpot){
                    closestPoint = cubeCenter;
                    closestSpot = cubeCenter.z;
                }
            }


            shouldAddCube = true;
            newCubeCenter = getTouchedCubeSide(closestPoint, ray.point, modelMatrix);


        }






        if (listener!= null){
            listener.onTouched(String.valueOf(normalizedX) + " : " + String.valueOf(normalizedY) + " : " + String.valueOf(cubePressed));
        }


    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to black.
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);

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





        gridShader = new GridShaderProgram(context);
        gridBuilder = new GridBuilder(-0f);
        gridBuilder.bindAttributesData();




        cubeShader = new ShaderProgram(context);
        builder = new FigureBuilder();
        builder.bindAttributesData();


        //cubePosition = builder.getCubeCenter();



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

        //Matrix.frustumM(projectionMatrix, 0, left, right, bottom, top, near, far);

        Matrix.orthoM(projectionMatrix, 0, left,right, bottom, top, near, far);




    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);



        //add new grid if needed
        if (lowerGrid){

            gridBuilder.lowerGrid();

            lowerGrid = false;

        }

        if(shouldAddCube){

            builder.addNewCube(newCubeCenter);

            shouldAddCube = false;

        }


        //manipulations with the cubes model matrix
        Matrix.setIdentityM(modelMatrix, 0);
        Matrix.translateM(modelMatrix, 0, 0.0f, 0.0f, -7.0f);

        rotateM(modelMatrix, 0, yAngle, 1f, 0f, 0f);
        rotateM(modelMatrix, 0, xAngle, 0f, 1f, 0f);


        //draw the grid
        gridShader.useProgram();


        gridShader.setUniforms(modelMatrix, viewMatrix, projectionMatrix);
        gridShader.setScaleFactor(scaleFactor);


        //gridBuilder.draw(gridShader);


        //draw the figure
        // push light behind the screen
        Matrix.setIdentityM(frontLightModelMatrix, 0);

        Matrix.translateM(frontLightModelMatrix, 0, 0.0f, 0.0f, -7.0f);

        rotateM(frontLightModelMatrix, 0, yAngle, 1f, 0f, 0f);
        rotateM(frontLightModelMatrix, 0, xAngle, 0f, 1f, 0f);

        System.arraycopy(frontLightModelMatrix ,0, backLightModelMatrix, 0, frontLightModelMatrix.length);
        //System.arraycopy(frontLightModelMatrix ,0, leftLightModelMatrix, 0, frontLightModelMatrix.length);
        System.arraycopy(frontLightModelMatrix ,0, rightLightModelMatrix, 0, frontLightModelMatrix.length);
        System.arraycopy(frontLightModelMatrix ,0, topLightModelMatrix, 0, frontLightModelMatrix.length);
        //System.arraycopy(frontLightModelMatrix ,0, bottomLightModelMatrix, 0, frontLightModelMatrix.length);


        float lightDistance = 100.0f;


        Matrix.translateM(frontLightModelMatrix, 0, 0.0f, 0.0f, lightDistance);
        Matrix.translateM(backLightModelMatrix, 0, 0.0f, 0.0f, -lightDistance);
        //Matrix.translateM(leftLightModelMatrix, 0, -lightDistance, 0.0f, 0.0f);
        Matrix.translateM(rightLightModelMatrix, 0, lightDistance, 0.0f, 0.0f);
        Matrix.translateM(topLightModelMatrix,  0, 0.0f, lightDistance, 0.0f);
        //Matrix.translateM(bottomLightModelMatrix, 0, 0.0f, -lightDistance, 0.0f);



        multiplyMV(frontLightPosInWorldSpace, 0, frontLightModelMatrix, 0, lightPosInModelSpace, 0);
        multiplyMV(frontLightPosInEyeSpace, 0, viewMatrix, 0, frontLightPosInWorldSpace, 0);

        multiplyMV(backLightPosInWorldSpace, 0, backLightModelMatrix, 0, lightPosInModelSpace, 0);
        multiplyMV(backLightPosInEyeSpace, 0, viewMatrix, 0, backLightPosInWorldSpace, 0);

//        Matrix.multiplyMV(leftLightPosInWorldSpace, 0, leftLightModelMatrix, 0, lightPosInModelSpace, 0);
//        Matrix.multiplyMV(leftLightPosInEyeSpace, 0, viewMatrix, 0, leftLightPosInWorldSpace, 0);

        multiplyMV(rightLightPosInWorldSpace, 0, rightLightModelMatrix, 0, lightPosInModelSpace, 0);
        multiplyMV(rightLightPosInEyeSpace, 0, viewMatrix, 0, rightLightPosInWorldSpace, 0);

        multiplyMV(topLightPosInWorldSpace, 0, topLightModelMatrix, 0, lightPosInModelSpace, 0);
        multiplyMV(topLightPosInEyeSpace, 0, viewMatrix, 0, topLightPosInWorldSpace, 0);

//        Matrix.multiplyMV(bottomLightPosInWorldSpace, 0, bottomLightModelMatrix, 0, lightPosInModelSpace, 0);
//        Matrix.multiplyMV(bottomLightPosInEyeSpace, 0, viewMatrix, 0, bottomLightPosInWorldSpace, 0);



        multiplyMM(viewProjectionMatrix, 0, viewMatrix, 0, projectionMatrix, 0);
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0);
        translateM(invertedViewProjectionMatrix, 0, 0f, 0f, -10f);

        MVPMatrix = getMVPMatrix(modelMatrix, viewMatrix, projectionMatrix);



        // Set our per-vertex lighting gridShader.
        cubeShader.useProgram();
        cubeShader.setUniforms(modelMatrix, viewMatrix, projectionMatrix,
                frontLightPosInEyeSpace,
                backLightPosInEyeSpace,
                leftLightPosInEyeSpace,
                rightLightPosInEyeSpace,
                topLightPosInEyeSpace,
                bottomLightPosInEyeSpace);
        cubeShader.setScaleFactor(scaleFactor);

        builder.draw(cubeShader);


    }

    private float[] getMVPMatrix(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix){

        float [] MVPMatrix = new float[16];
        Matrix.multiplyMM(MVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(MVPMatrix, 0, projectionMatrix, 0, MVPMatrix, 0);

        return MVPMatrix;

    }


}

package com.testing.vladyslav.cubes.animation;

import android.opengl.GLES20;

import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.objects.Cube;
import com.testing.vladyslav.cubes.programs.ShaderProgram;

import java.util.ArrayList;

import static android.opengl.GLES20.glDrawArrays;

public class Animator {

    private float animationDuration = 500;
    private int animationFramesAmount = 60;
    private float framesPerMS = (float)animationFramesAmount / animationDuration;
    private long animationStartTime;
    private int framesDrawn = -1;


    private boolean animationStretchIsRunning = false;
    private boolean animationSqueezeIsRunning = false;

    private boolean animationIsRunning = false;
    private boolean figureIsOpened = false;

    private float[] scatterVector = {0.0f, 0.0f, 0.0f};
    private float[] resetScatterVector = {0.0f, 0.0f, 0.0f};

    private final int cubeNumber;
    private final int objectHeight;
    private final ArrayList<Cube> cubes;

    public boolean animationIsRunning(){return animationIsRunning;}

    public Animator(int cubeNumber, int objectHeight, ArrayList<Cube> cubes){
        this.objectHeight = objectHeight;
        this.cubes = cubes;
        this.cubeNumber = cubeNumber;
    }


    public void drawOpenedFigure(ShaderProgram shader){

        if(animationSqueezeIsRunning){
            drawClosedFigure(shader);
            return;
        }

        if (!figureIsOpened ){
            animateStretch(shader);
        }else{
            float currentY = cubes.get(cubes.size()-1).center.y;
            scatterVector = new float[]{0.0f, objectHeight/2, 0.0f};

            int cubesToDraw = 0;

            for (int i = 0; i< cubes.size(); i++){


                Cube cube = cubes.get(cubes.size()-1 - i);
                if (cube.center.y != currentY || i == cubes.size()-1){

                    if (i == (cubes.size()-1)){ cubesToDraw++; i++;};

                    currentY = cube.center.y;
                    shader.setScatter(scatterVector);

                    GLES20.glDrawArrays(GLES20.GL_TRIANGLES, CubeDataHolder.getInstance().sizeInVertex * (cubeNumber-i), CubeDataHolder.getInstance().sizeInVertex * cubesToDraw);

                    scatterVector[1] = scatterVector[1]-1;
                    cubesToDraw = 0;
                }
                cubesToDraw++;
            }
        }

    };
    public void drawClosedFigure(ShaderProgram shader){

        if (animationStretchIsRunning){
            drawOpenedFigure(shader);
            return;
        }

        if (figureIsOpened){
            animateSqueeze(shader);
        }else{
            shader.setScatter(resetScatterVector);
            glDrawArrays(GLES20.GL_TRIANGLES, 0, CubeDataHolder.getInstance().sizeInVertex * cubeNumber);
        }


    };

    public void animateStretch(ShaderProgram shader){

        //don!t run the animation if the figure is stretched
        if (figureIsOpened) return;

        //set animation start time
        if (!animationStretchIsRunning) {
            animationStretchIsRunning = true;
            animationStartTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        long timePast = (currentTime - animationStartTime);

        //define the current frame
        float frame = Math.round(timePast * framesPerMS);

        //turn of the animation if all the frames are drawn
        if (frame > animationFramesAmount) {
            animationStretchIsRunning = false;
            figureIsOpened = true;
            framesDrawn = -1;
            drawOpenedFigure(shader);
            return;
        }

        //update drawn frames amount
        if (frame > framesDrawn)  framesDrawn++; else return;

        float animationPercentage = frame / animationFramesAmount;

        float currentY = cubes.get(cubes.size()-1).center.y;
        scatterVector = new float[]{0.0f, objectHeight/2, 0.0f};


        int cubesToDraw = 0;

        for (int i = 0; i< cubes.size(); i++){


            Cube cube = cubes.get(cubes.size()-1 - i);
            if (cube.center.y != currentY || i == cubes.size()-1){

                if (i == (cubes.size()-1)){ cubesToDraw++; i++;};

                currentY = cube.center.y;
                shader.setScatter(new float[]{scatterVector[0], scatterVector[1] * animationPercentage, scatterVector[2]});

                glDrawArrays(GLES20.GL_TRIANGLES, CubeDataHolder.getInstance().sizeInVertex * (cubeNumber-i), CubeDataHolder.getInstance().sizeInVertex * cubesToDraw);

                scatterVector[1] = scatterVector[1]-1;
                cubesToDraw = 0;
            }
            cubesToDraw++;
        }

    }

    public void animateSqueeze(ShaderProgram shader){

        //don!t run the animation if the figure is stretched
        if (!figureIsOpened) return;

        //set animation start time
        if (!animationSqueezeIsRunning) {
            animationSqueezeIsRunning = true;
            animationStartTime = System.currentTimeMillis();
        }

        long currentTime = System.currentTimeMillis();
        long timePast = (currentTime - animationStartTime);

        //define the current frame
        float frame = Math.round(timePast * framesPerMS);

        //turn of the animation if all the frames are drawn
        if (frame > animationFramesAmount) {
            animationSqueezeIsRunning = false;
            figureIsOpened = false;
            framesDrawn = -1;
            drawClosedFigure(shader);
            return;
        }

        //update drawn frames amount
        if (frame > framesDrawn)  framesDrawn++; else return;

        float animationPercentage = (animationFramesAmount - frame) / animationFramesAmount;

        float currentY = cubes.get(cubes.size()-1).center.y;
        scatterVector = new float[]{0.0f, objectHeight/2, 0.0f};


        int cubesToDraw = 0;

        for (int i = 0; i< cubes.size(); i++){


            Cube cube = cubes.get(cubes.size()-1 - i);
            if (cube.center.y != currentY || i == cubes.size()-1){

                if (i == (cubes.size()-1)){ cubesToDraw++; i++;};

                currentY = cube.center.y;
                shader.setScatter(new float[]{scatterVector[0], scatterVector[1] * animationPercentage, scatterVector[2]});

                glDrawArrays(GLES20.GL_TRIANGLES, CubeDataHolder.getInstance().sizeInVertex * (cubeNumber-i), CubeDataHolder.getInstance().sizeInVertex * cubesToDraw);

                scatterVector[1] = scatterVector[1]-1;
                cubesToDraw = 0;
            }
            cubesToDraw++;
        }
    }


}

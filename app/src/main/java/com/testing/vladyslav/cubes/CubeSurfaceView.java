package com.testing.vladyslav.cubes;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

public class CubeSurfaceView extends GLSurfaceView{

    private float scaleFactor = 1.f;

    private final float maxXAngle = 90f;
    private final float minXAngle = -90f;

    private int firstPointerIndex = 0;
    private int secondPointerIndex = 1;

    private float previousTouchCenterX = 0f;
    private float previousTouchCenterY = 0f;

    private float touchCenterX = 0f;
    private float touchCenterY = 0f;

    private float figureStrideX = 0f;
    private float figureStrideY = 0f;

    private float startingDistanceBetweenPointers;
    private float movingSensitivity = 15f;

    private int screenWidth = 0;
    private int screenHeight = 0;

    private final int SCALE = 1;
    private final int MOVE = 2;

    private boolean twoPointersDetected = false;

    private int currentAction = MOVE;

    //the figure will start scale when distance between pointers increases by this parameter %
    //0.1f means distance between fingers should dbe increased ast least by 10% of the screen width
    private float twoPointersMovementScaleBound = 0.1f;
    private float twoPointersMovementDistance = getX() * twoPointersMovementScaleBound;


    private CubeRenderer renderer;

    int pointerIndex = -1;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 900;
    private final float ROTATE_START_POINT = 10f;
    private float mPreviousX;
    private float mPreviousY;

    private float startX;
    private float startY;

    final DisplayMetrics displayMetrics = new DisplayMetrics();

    private GesturesListener gesturesListener;
    public interface GesturesListener{
        void log(String s);
    }

    public void setGesturesListener(GesturesListener listener){
        this.gesturesListener = listener;
    }

    ScaleGestureDetector mScaleDetector;



    public CubeRenderer getRenderer(){

        return this.renderer;

    }

    public CubeSurfaceView(Context context) {
        super(context);

        setEGLContextClientVersion(2);
        //setEGLConfigChooser(new MyConfigChooser());


        this.renderer = new CubeRenderer(context);

        setRenderer(this.renderer);
    }

    public void setNewRenderer(Context context){
        this.renderer = new CubeRenderer(context);
        //setRenderer(this.renderer);
    }

    public CubeSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setEGLContextClientVersion(2);

        this.renderer = new CubeRenderer(context);

        mScaleDetector = new ScaleGestureDetector(getContext(), new PinchListener());

        screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;

        twoPointersMovementDistance = screenWidth * twoPointersMovementScaleBound;

        if(Settings.antialiasing){
            setEGLConfigChooser(new MyConfigChooser());
        }


        setRenderer(this.renderer);

    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {



        if (e.getPointerCount()>1){


            int xPointer1 = (int) e.getX(firstPointerIndex);
            int yPointer1 = (int) e.getY(firstPointerIndex);

            int xPointer2 = (int) e.getX(secondPointerIndex);
            int yPointer2 = (int) e.getY(secondPointerIndex);

            float distanceBetweenPointers = 0f;

            if(e.getActionMasked() == ACTION_MOVE){

                twoPointersDetected = true;

                touchCenterX = (xPointer2 + xPointer1) / 2f;
                touchCenterY = (yPointer2 + yPointer1) / 2f;

                if(startingDistanceBetweenPointers == 0f){
                    startingDistanceBetweenPointers = (float)Math.sqrt(Math.pow((xPointer2 - xPointer1), 2) + Math.pow((yPointer2 - yPointer1), 2));
                    previousTouchCenterX = touchCenterX;
                    previousTouchCenterY = touchCenterY;


                }
                distanceBetweenPointers = (float)Math.sqrt(Math.pow((xPointer2 - xPointer1), 2) + Math.pow((yPointer2 - yPointer1), 2));
            }

            if(twoPointersDetected){
                currentAction = Math.abs(distanceBetweenPointers - startingDistanceBetweenPointers) > twoPointersMovementDistance? SCALE : MOVE;

                //log on the display
                if(gesturesListener != null){
                    String text = "First: " + String.valueOf(xPointer1) + " : " + String.valueOf(yPointer1) + "\n"
                            +"Second: " + String.valueOf(xPointer2) + " : " + String.valueOf(yPointer2) + "\n"
                            +"Distance: " + String.valueOf(distanceBetweenPointers) + "\n"
                            +"Action: " + (currentAction == SCALE? "scale" : "move") + "\n"
                            +"Scale: " + String.valueOf(scaleFactor);
                    gesturesListener.log(text);
                }


                float touchCenterDX = touchCenterX - previousTouchCenterX;
                float touchCenterDY = touchCenterY - previousTouchCenterY;

                figureStrideX += touchCenterDX / screenWidth * movingSensitivity;
                figureStrideY += touchCenterDY / screenHeight * movingSensitivity;

//                figureStrideX += touchCenterDX ;
//                figureStrideY += touchCenterDY ;


                float normalizedStrideX = (figureStrideX / (float) getWidth()) * 2 - 1;
                float normalizedStrideY = (figureStrideY / (float) getHeight()) * 2 - 1;

                renderer.setStride(figureStrideX, figureStrideY);

                previousTouchCenterX = touchCenterX;
                previousTouchCenterY = touchCenterY;

                mScaleDetector.onTouchEvent(e);


//                if(currentAction == SCALE){
//                    mScaleDetector.onTouchEvent(e);
//                }else{
//
//                    float touchCenterDX = touchCenterX - previousTouchCenterX;
//                    float touchCenterDY = touchCenterY - previousTouchCenterY;
//
//                    figureStrideX += touchCenterDX / screenWidth * movingSensitivity;
//                    figureStrideY += touchCenterDY / screenHeight * movingSensitivity;
//
//
//                    renderer.setStride(figureStrideX, figureStrideY);
//
//                    previousTouchCenterX = touchCenterX;
//                    previousTouchCenterY = touchCenterY;
//
//                }
            }






        }else{

            switch (e.getActionMasked()) {
                case ACTION_DOWN :{

                    twoPointersDetected = false;
                    pointerIndex = pointerIndex == -1? e.getActionIndex(): pointerIndex;

                    final int pointerIndex = e.getActionIndex();
                    mPreviousX = e.getX(pointerIndex);
                    mPreviousY = e.getY(pointerIndex);

                    startX = mPreviousX;
                    startY = mPreviousY;


                    break;

                }


                case ACTION_MOVE:{


                    if(twoPointersDetected){
                        break;
                    }

                    float x = e.getX(pointerIndex);
                    float y = e.getY(pointerIndex);

                    float dx = x - mPreviousX;
                    float dy = y - mPreviousY;


                    renderer.setXAngle(renderer.getXAngle() + dx * TOUCH_SCALE_FACTOR);
                    renderer.setYAngle(renderer.getYAngle() + dy * TOUCH_SCALE_FACTOR);

                    mPreviousX = x;
                    mPreviousY = y;


                    requestRender();
                    break;
                }

                case ACTION_UP:{

                    resetPointersValues();

                    float x = e.getX(pointerIndex);
                    float y = e.getY(pointerIndex);

                    float dx = x - startX;
                    float dy = y - startY;

                    if (Math.abs(dx) < ROTATE_START_POINT && Math.abs(dy) < ROTATE_START_POINT){


                        float normalizedX = (e.getX() / (float) getWidth()) * 2 - 1;
                        float normalizedY = -((e.getY() / (float) getHeight()) * 2 - 1);

                        renderer.handleTouchPress(normalizedX, normalizedY);
                    }

                }
            }



        }

        return true;
    }


    private void resetPointersValues(){

        startingDistanceBetweenPointers = 0f;
        previousTouchCenterX = 0f;
        previousTouchCenterY = 0f;

    }


    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();


            // Don't let the object get too small or too large.
            scaleFactor = Math.max(Settings.minimumFigureScale, Math.min(scaleFactor, Settings.maximumFigureScale));

            renderer.setScaleFactor(scaleFactor);

            twoPointersDetected = true;

            return true;
        }

    }

    class MyConfigChooser implements GLSurfaceView.EGLConfigChooser {
        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int attribs[] = {
                    EGL10.EGL_LEVEL, 0,
                    EGL10.EGL_RENDERABLE_TYPE, 4,  // EGL_OPENGL_ES2_BIT
                    EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                    EGL10.EGL_RED_SIZE, 8,
                    EGL10.EGL_GREEN_SIZE, 8,
                    EGL10.EGL_BLUE_SIZE, 8,
                    EGL10.EGL_DEPTH_SIZE, 16,
                    EGL10.EGL_SAMPLE_BUFFERS, 1,
                    EGL10.EGL_SAMPLES, 4,  // This is for 4x MSAA.
                    EGL10.EGL_NONE
            };
            EGLConfig[] configs = new EGLConfig[1];
            int[] configCounts = new int[1];
            egl.eglChooseConfig(display, attribs, configs, 1, configCounts);

            if (configCounts[0] == 0) {
                // Failed! Error handling.
                return null;
            } else {
                return configs[0];
            }
        }
    }

}

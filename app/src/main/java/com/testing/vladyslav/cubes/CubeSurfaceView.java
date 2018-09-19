package com.testing.vladyslav.cubes;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.testing.vladyslav.cubes.presenters.StudioActivityPresenter;

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

    private boolean isScaling = false;

    private CubeRenderer renderer;

    int pointerIndex = -1;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 900;
    private final float ROTATE_START_POINT = 10f;
    private float mPreviousX;
    private float mPreviousY;

    private float startX;
    private float startY;

    final DisplayMetrics displayMetrics = new DisplayMetrics();

    ScaleGestureDetector mScaleDetector;

    private StudioActivityPresenter.OnFigureChangeListener listener;

    public void setOnFigureChangedListener(StudioActivityPresenter.OnFigureChangeListener listener){
        this.listener = listener;
    }



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


        setRenderer(this.renderer);
    }



    @Override
    public boolean onTouchEvent(MotionEvent e) {



        if (e.getPointerCount()>1){
            isScaling = true;
            mScaleDetector.onTouchEvent(e);
        }else{

            switch (e.getActionMasked()) {
                case ACTION_DOWN :{

                    isScaling = false;
                    pointerIndex = pointerIndex == -1? e.getActionIndex(): pointerIndex;

                    final int pointerIndex = e.getActionIndex();
                    mPreviousX = e.getX(pointerIndex);
                    mPreviousY = e.getY(pointerIndex);

                    startX = mPreviousX;
                    startY = mPreviousY;


                    break;

                }


                case ACTION_MOVE:{


                    if(isScaling){
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

                    float x = e.getX(pointerIndex);
                    float y = e.getY(pointerIndex);

                    float dx = x - startX;
                    float dy = y - startY;

                    if (Math.abs(dx) < ROTATE_START_POINT && Math.abs(dy) < ROTATE_START_POINT){


                        float normalizedX = (e.getX() / (float) getWidth()) * 2 - 1;
                        float normalizedY = -((e.getY() / (float) getHeight()) * 2 - 1);

                        renderer.handleTouchPress(normalizedX, normalizedY);
                        if(listener != null)
                        listener.onFigureChanged();
                    }

                }
            }



        }

        return true;
    }




    private class PinchListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();


            // Don't let the object get too small or too large.
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 6.0f));

            renderer.setScaleFactor(scaleFactor);

            isScaling = true;

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

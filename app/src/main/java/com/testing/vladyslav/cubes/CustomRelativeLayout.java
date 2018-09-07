package com.testing.vladyslav.cubes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.testing.vladyslav.cubes.activities.StudioActivity;

public class CustomRelativeLayout extends RelativeLayout{

    private OnColorTouchListener listener;
    private Activity activity;

    public interface OnColorTouchListener{

        void onTouch(int colorPosition);

    }

    public CustomRelativeLayout(Context context) {
        super(context);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setOnColorTouchListener(OnColorTouchListener listener){

        this.listener = listener;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int colorsNumber = 16;


        float x = event.getRawX();
        float y = event.getRawY();

        Display display = activity.getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int screenWidth = size. x;
        float floatColorSize = (float)screenWidth / colorsNumber;

        if(y < (float)size.y - floatColorSize){
            return false;
        }

        int colorPosition = 0;

        for (int i = 1; i<= colorsNumber; i++){
            if (x < i*floatColorSize){
                colorPosition = i-1;
                break;
            }
        }


        if(listener != null){
            listener.onTouch(colorPosition);
        }

        return true;

    }
}

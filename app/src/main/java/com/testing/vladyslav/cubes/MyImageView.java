package com.testing.vladyslav.cubes;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyImageView extends ImageView
{
    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        Resources r = getResources();

        int imageHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics()));
        int imageWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics()));



//        resetColorsLayout();
//        surfaceView.getRenderer().setColor(colorOrder[position]);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth
                , imageHeight);
        params.weight = 0.0f;
        setLayoutParams(params);
//        view.setBackground(getResources().getDrawable(R.drawable.color_shadow));
//
//
//        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//        float translationZ = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
//
//        view.setElevation(elevation);
//        view.setTranslationZ(translationZ);
//
//        return true;

        return true;
    }
}

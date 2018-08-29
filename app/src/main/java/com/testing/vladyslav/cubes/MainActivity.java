package com.testing.vladyslav.cubes;

import android.app.ActionBar;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.vladyslav.cubes.data.CubeDataHolder;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements CubeRenderer.CubeRendererListener{


    private ImageView img_cancel;
    private ImageView img_repeat;
    private ImageView img_add;
    private ImageView img_change_color;
    private ImageView img_delete;

    private ImageView img_color_turcouse;
    private ImageView img_color_green;
    private ImageView img_color_light_green;
    private ImageView img_color_yellow;
    private ImageView img_color_orange;
    private ImageView img_color_red;
    private ImageView img_color_pink;
    private ImageView img_color_pirple;
    private ImageView img_color_blue;
    private ImageView img_color_light_blue;
    private ImageView img_color_brown;
    private ImageView img_color_light_brown;
    private ImageView img_color_tan;
    private ImageView img_color_white;
    private ImageView img_color_grey;
    private ImageView img_color_black;

    private RelativeLayout editor_color_row;

    private TextView txt_isTouched;

    private int graphicsQuality = 1;
    private int nonTransparentAlpha = 255;

    private CubeSurfaceView surfaceView;
    private ArrayList<ImageView> colorRow;

    private short[] colorOrder = new short[]{243, 244, 245, 246, 247, 248, 250, 249, 241, 242, 253, 252, 251, 254, 255, 240};

    public static final HashMap<Integer, Integer> colorCodeToImageName = new HashMap<Integer, Integer>(){
        {

            put(243, R.mipmap.color_16); //turquoise
            put(244, R.mipmap.color_15); //green
            put(245, R.mipmap.color_14); //light green
            put(246, R.mipmap.color_13); //yellow

            put(247, R.mipmap.color_12); //orange
            put(248, R.mipmap.color_11); //red
            put(250, R.mipmap.color_10); //pink
            put(249, R.mipmap.color_09); //violet

            put(241, R.mipmap.color_08); //blue
            put(242, R.mipmap.color_07); //light blue
            put(253, R.mipmap.color_06); //brown
            put(252, R.mipmap.color_05); //light brown

            put(251, R.mipmap.color_04); //tan
            put(254, R.mipmap.color_03); //white
            put(255, R.mipmap.color_02); //grey
            put(240, R.mipmap.color_01); //black
        }};

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        int quality = getIntent().getIntExtra("quality", 1);

        switch (quality){

            case 0:{
                CubeDataHolder.getInstance().setFacetList(CubeDataHolder.getInstance().facetListLow);
                break;
            }
            case 1:{
                CubeDataHolder.getInstance().setFacetList(CubeDataHolder.getInstance().facetListMedium);
                break;
            }
            case 2:{
                CubeDataHolder.getInstance().setFacetList(CubeDataHolder.getInstance().facetListHigh);
                break;
            }

        }

        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceView);


        img_cancel = findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().backward();
            }
        });
        img_repeat = findViewById(R.id.img_repeat);
        img_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().forward();
            }
        });

        img_add = findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setBuildingMode();
                makeTransparentMiddleButtons();
                img_add.setImageAlpha(nonTransparentAlpha);

            }
        });
        img_change_color = findViewById(R.id.img_change_color);
        img_change_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColorEditingMode();
                makeTransparentMiddleButtons();
                img_change_color.setImageAlpha(nonTransparentAlpha);
            }
        });
        img_delete = findViewById(R.id.img_delete);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setDeleteMode();
                makeTransparentMiddleButtons();
                img_delete.setImageAlpha(nonTransparentAlpha);
            }
        });



        txt_isTouched = findViewById(R.id.txt_isTouched);

//        colorRow = new ArrayList<>();
//        colorRow.add(img_color_turcouse);
//        colorRow.add(img_color_green);
//        colorRow.add(img_color_light_green);
//        colorRow.add(img_color_yellow);
//
//        colorRow.add(img_color_orange);
//        colorRow.add(img_color_red);
//        colorRow.add(img_color_pink);
//        colorRow.add(img_color_pirple);
//
//        colorRow.add(img_color_blue);
//        colorRow.add(img_color_light_blue);
//        colorRow.add(img_color_brown);
//        colorRow.add(img_color_light_brown);
//
//        colorRow.add(img_color_tan);
//        colorRow.add(img_color_white);
//        colorRow.add(img_color_grey);
//        colorRow.add(img_color_black);



        surfaceView.setListener(this);

        makeTransparentMiddleButtons();

//        for (int i = 0; i< colorRow.size(); i++){
//
//            ImageView image = colorRow.get(i);
//
//            final int position = i;
//
//            image.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Resources r = getResources();
//
//                    int imageHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics()));
//                    int imageWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, r.getDisplayMetrics()));
//
//                    resetColorsLayout();
//                    surfaceView.getRenderer().setColor(colorOrder[position]);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth
//                            , imageHeight);
//                    params.weight = 0.0f;
//                    view.setLayoutParams(params);
//                    view.setBackground(getResources().getDrawable(R.drawable.improved_shadow));
//
//
//                    float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
//                    float translationZ = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
//
//                    view.setElevation(elevation);
//                    //view.setTranslationZ(translationZ);
//
//                }
//            });
//
//
//
//
//
//
//        }

        //resetColorsLayout();

        createColorRowLayout();

        surfaceView.getRenderer().setBuildingMode();
        img_add.setImageAlpha(nonTransparentAlpha);



    }

    private void createColorRowLayout(){

        editor_color_row = findViewById(R.id.editor_colors_row);
        colorRow = new ArrayList<>();



        Resources r = getResources();

        int colorsNumber = 16;

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int screenWidth = size. x;
        final int intColorWidth = Math.round((float)screenWidth / colorsNumber);
        final int intColorHeight = intColorWidth;

        float floatColorSize = (float)screenWidth / colorsNumber;

        for (int i = 0; i< 16; i++){
            ImageView image = new ImageView(this);
            final int color = colorOrder[i];
            int resId = colorCodeToImageName.get(color);
            Drawable d = r.getDrawable(resId);

            image.setImageDrawable(d);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(intColorWidth, intColorHeight);
            params.setMargins(i == colorsNumber-1? screenWidth - intColorWidth:Math.round(floatColorSize*i), 0, 0, 0);


            image.setLayoutParams(params);


            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    resetColorsLayout();


                    surfaceView.getRenderer().setColor(color);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    params.width += 10;
                    params.height += 10;
                    view.setElevation(10);

                    view.setLayoutParams(params);
                    //params.setMargins(i == colorsNumber-1? screenWidth - intColorWidth:Math.round(floatColorSize*i), 0, 0, 0);

                }
            });


            colorRow.add(image);
            editor_color_row.addView(image);
        }



    }

    private void resetColorsLayout(){

        int colorsNumber = 16;

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int screenWidth = size. x;
        final int intColorWidth = Math.round((float)screenWidth / colorsNumber);
        final int intColorHeight = intColorWidth;
        float floatColorSize = (float)screenWidth / colorsNumber;

        for (int i = 0; i< colorRow.size(); i++){

            ImageView image = colorRow.get(i);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(intColorWidth, intColorHeight);
            params.setMargins(i == colorsNumber-1? screenWidth - intColorWidth:Math.round(floatColorSize*i), 0, 0, 0);

            image.setElevation(0f);

            image.setLayoutParams(params);



        }
    }

    private void makeTransparentMiddleButtons(){

        int alpha = 90;

        img_delete.setImageAlpha(alpha);
        img_add.setImageAlpha(alpha);
        img_change_color.setImageAlpha(alpha);


    }

    public void setTxt_isTouchedText(String txt){

//        if(txt_isTouched != null && txt != null)
//            txt_isTouched.setText(txt);

    }

    @Override
    public void onTouched(String txt) {
        setTxt_isTouchedText(txt);
    }
}

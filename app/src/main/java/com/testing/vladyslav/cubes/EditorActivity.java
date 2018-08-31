package com.testing.vladyslav.cubes;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.database.UserModelsDBLoader;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.dialogs.EnterFigureNameDialog;
import com.testing.vladyslav.cubes.presenters.EditorActivityPresenter;

import java.util.ArrayList;
import java.util.HashMap;


public class EditorActivity extends AppCompatActivity implements CubeRenderer.CubeRendererListener, EditorActivityPresenter.EditorActivityView {


    private ImageView img_cancel;
    private ImageView img_repeat;
    private ImageView img_add;
    private ImageView img_change_color;
    private ImageView img_delete;

    private ImageView img_shadow_right;
    private ImageView img_shadow_left;

    private CustomRelativeLayout editor_color_row;

    private TextView txt_isTouched;
    private LinearLayout menu;
    RelativeLayout fullscreen;

    private int colorsNumber = 16;

    private int graphicsQuality = 1;
    private int nonTransparentAlpha = 255;

    private CubeSurfaceView surfaceView;
    private ArrayList<ImageView> colorRow;

    private UserModel userModel;
    private EditorActivityPresenter presenter;

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

        UserModelsDBLoader modelsDBModel = new UserModelsDBLoader(getApplicationContext());
        presenter = new EditorActivityPresenter(modelsDBModel);
        presenter.attachView(this);

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

        userModel = new UserModel();

        userModel.setName("Patronus");
        userModel.setCubeNumber(5);
        userModel.setSizeX(3);
        userModel.setSizeY(3);
        userModel.setSizeZ(3);
        userModel.setCubes("A hell lot of cubes");


        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceView);
        menu = findViewById(R.id.menu_layout);
        menu.setVisibility(View.INVISIBLE);
        fullscreen = findViewById(R.id.fullscreen_layout);
        fullscreen.setVisibility(View.INVISIBLE);

        findViewById(R.id.img_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menu.setVisibility(menu.getVisibility() == View.INVISIBLE? View.VISIBLE: View.INVISIBLE);
                fullscreen.setVisibility(menu.getVisibility() == View.INVISIBLE? View.VISIBLE: View.INVISIBLE);
            }
        });

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

        surfaceView.setListener(this);

        makeTransparentMiddleButtons();

        createColorRowLayout();

        surfaceView.getRenderer().setBuildingMode();
        img_add.setImageAlpha(nonTransparentAlpha);



    }

    public void saveAsClicked(View v){

        EnterFigureNameDialog dialog = new EnterFigureNameDialog(EditorActivity.this);
        dialog.setListener(new EnterFigureNameDialog.FigureNameDialogListener() {
            @Override
            public void enterFigureNamePressed(String name) {

                presenter.saveUserModel(name);

            }
        });
        dialog.show();

    }


    public void openClicked(View v){

        presenter.openUserModel();

    }

    public void saveClicked(View v){

    }


    private void createColorRowLayout(){

        editor_color_row = findViewById(R.id.editor_colors_row);
        editor_color_row.setActivity(this);
        editor_color_row.setOnColorTouchListener(new CustomRelativeLayout.OnColorTouchListener() {
            @Override
            public void onTouch(int colorPosition) {
                selectColor(colorPosition);
                setShadows(colorPosition);
            }
        });

        colorRow = new ArrayList<>();

        Resources r = getResources();

        for (int i = 0; i< colorsNumber; i++){
            ImageView image = new ImageView(this);
            final int color = colorOrder[i];
            int resId = colorCodeToImageName.get(color);
            Drawable d = r.getDrawable(resId);

            image.setImageDrawable(d);

            colorRow.add(image);
            editor_color_row.addView(image);
        }

        img_shadow_left = new ImageView(this);
        img_shadow_left.setImageDrawable(getResources().getDrawable(R.drawable.shadow_left));

        img_shadow_right = new ImageView(this);
        img_shadow_right.setImageDrawable(getResources().getDrawable(R.drawable.shadow_right));

        editor_color_row.addView(img_shadow_left);
        editor_color_row.addView(img_shadow_right);
        resetColorsLayout();
        setShadows(0);

        img_shadow_left.setVisibility(View.INVISIBLE);
        img_shadow_right.setVisibility(View.INVISIBLE);


    }

    private void selectColor(int colorPosition){

        float scaleSize = 1.4f;
        int elevation = 10;
        int shadowSize = 0;

        ImageView image = colorRow.get(colorPosition);

        resetColorsLayout();

        surfaceView.getRenderer().setColor(colorOrder[colorPosition]);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();

        //shadow
        image.setElevation(elevation);

        //size
        image.setTranslationY(params.height - params.height * scaleSize + shadowSize + elevation);
        int deltaWidth = Math.round(params.height * scaleSize) - params.height;

        params.width = Math.round(params.width * scaleSize);
        params.height = Math.round(params.height * scaleSize);


        //margins
        int marginBottom = Math.round(params.height - params.height * scaleSize);
        int marginLeft = params.leftMargin;
        int marginRight = params.rightMargin;

        if (colorPosition == 0) {
            marginLeft = -(shadowSize + elevation);
        }else if(colorPosition == colorOrder.length-1){
            marginLeft -= (shadowSize + elevation + Math.round(deltaWidth/8));
            marginRight = -(shadowSize + elevation);
        }else{

            marginLeft = Math.round((float)params.leftMargin - (float)deltaWidth /2);

        }

        params.setMargins(marginLeft, params.topMargin, marginRight, marginBottom);
        image.setLayoutParams(params);

    }

    private void setShadows (int selectedColorPosition){

        img_shadow_left.setVisibility(View.VISIBLE);
        img_shadow_right.setVisibility(View.VISIBLE);

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int screenWidth = size. x;
        final int intColorWidth = Math.round((float)screenWidth / colorsNumber);
        final int intColorHeight = intColorWidth;

        float floatColorSize = (float)screenWidth / colorsNumber;

        RelativeLayout.LayoutParams leftShadowParams = new RelativeLayout.LayoutParams(intColorWidth, intColorHeight);
        RelativeLayout.LayoutParams rightShadowParams = new RelativeLayout.LayoutParams(intColorWidth, intColorHeight);

        leftShadowParams.setMargins(Math.round(floatColorSize * (selectedColorPosition-1)), 0, 0, 0);
        rightShadowParams.setMargins(Math.round(floatColorSize * (selectedColorPosition+1)), 0, 0, 0);

        img_shadow_right.setLayoutParams(rightShadowParams);
        img_shadow_left.setLayoutParams(leftShadowParams);

        if(selectedColorPosition == 0){
            img_shadow_left.setVisibility(View.INVISIBLE);
        }else if(selectedColorPosition == colorRow.size()) {
            img_shadow_right.setVisibility(View.INVISIBLE);
        }


    }

    private int viewToPosition(View v){

        return  colorRow.indexOf((ImageView)v);

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

            image.setPadding(0, 0, 0, 0);

            image.setElevation(0f);
            image.setTranslationY(0f);

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

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
}

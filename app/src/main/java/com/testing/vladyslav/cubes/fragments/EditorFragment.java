package com.testing.vladyslav.cubes.fragments;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.CubeSurfaceView;
import com.testing.vladyslav.cubes.CustomRelativeLayout;
import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.activities.StudioActivity;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.dialogs.EnterFigureNameDialog;
import com.testing.vladyslav.cubes.presenters.StudioActivityPresenter;

import java.util.ArrayList;
import java.util.HashMap;

public class EditorFragment extends Fragment implements StudioActivityPresenter.EditorFragmentView{


    private ImageView img_cancel;
    private ImageView img_repeat;
    private ImageView img_add;
    private ImageView img_change_color;
    private ImageView img_delete;

    private RelativeLayout menu;
    private ImageView img_menu_open;
    private ImageView img_menu_close;

    private ImageView img_shadow_right;
    private ImageView img_shadow_left;

    private TextView txt_save;
    private TextView txt_save_as;
    private TextView txt_open;

    private CustomRelativeLayout editor_color_row;


    private int colorsNumber = 16;
    private int nonTransparentAlpha = 255;

    private CubeSurfaceView surfaceView;
    private ArrayList<ImageView> colorRow;

    private StudioActivityPresenter presenter;

    private UserModel modelToRender;
    private View view;


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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editor, container, false);

        surfaceView = view.findViewById(R.id.surfaceView);
        if(modelToRender != null){
            surfaceView.getRenderer().setRenderingModel(modelToRender);
        }
        menu = view.findViewById(R.id.editorMenu);
        menu.setVisibility(View.INVISIBLE);
        img_menu_open = view.findViewById(R.id.ic_menu_open);
        img_menu_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenu();
            }
        });
        img_menu_close = view.findViewById(R.id.ic_menu_close);
        img_menu_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });
        menu.setVisibility(View.INVISIBLE);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenu();
            }
        });


        img_cancel = view.findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().backward();
            }
        });
        img_repeat = view.findViewById(R.id.img_repeat);
        img_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().forward();
            }
        });

        img_add = view.findViewById(R.id.img_add);
        img_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setBuildingMode();
                makeTransparentMiddleButtons();
                img_add.setImageAlpha(nonTransparentAlpha);

            }
        });
        img_change_color = view.findViewById(R.id.img_change_color);
        img_change_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColorEditingMode();
                makeTransparentMiddleButtons();
                img_change_color.setImageAlpha(nonTransparentAlpha);
            }
        });
        img_delete = view.findViewById(R.id.img_delete);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setDeleteMode();
                makeTransparentMiddleButtons();
                img_delete.setImageAlpha(nonTransparentAlpha);
            }
        });


        txt_open = view.findViewById(R.id.txt_open);
        txt_save = view.findViewById(R.id.txt_save);
        txt_save_as = view.findViewById(R.id.txt_save_as);

        txt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.openClicked();
            }
        });


        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.saveClicked();
            }
        });
        txt_save_as.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDialogBox();

            }
        });

        surfaceView.setListener(new CubeRenderer.CubeRendererListener() {
            @Override
            public void onTouched(String txt) {

            }
        });

        makeTransparentMiddleButtons();

        createColorRowLayout();

        surfaceView.getRenderer().setBuildingMode();
        img_add.setImageAlpha(nonTransparentAlpha);




        return view;
    }

    public void setPresenter(StudioActivityPresenter presenter){this.presenter = presenter;}

    private void createColorRowLayout(){

        editor_color_row = view.findViewById(R.id.editor_colors_row);
        editor_color_row.setActivity(this.getActivity());
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
            ImageView image = new ImageView(this.getContext());
            final int color = colorOrder[i];
            int resId = colorCodeToImageName.get(color);
            Drawable d = r.getDrawable(resId);

            image.setImageDrawable(d);

            colorRow.add(image);
            editor_color_row.addView(image);
        }

        img_shadow_left = new ImageView(this.getContext());
        img_shadow_left.setImageDrawable(getResources().getDrawable(R.drawable.shadow_left));

        img_shadow_right = new ImageView(this.getContext());
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

        Display display = getActivity().getWindowManager(). getDefaultDisplay();
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

        Display display = getActivity().getWindowManager(). getDefaultDisplay();
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

    private void openMenu(){
        menu.setVisibility(View.VISIBLE);
        img_menu_open.setVisibility(View.INVISIBLE);
    }

    private void closeMenu(){
        menu.setVisibility(View.INVISIBLE);
        img_menu_open.setVisibility(View.VISIBLE);
    }

    private void makeTransparentMiddleButtons(){

        int alpha = 90;

        img_delete.setImageAlpha(alpha);
        img_add.setImageAlpha(alpha);
        img_change_color.setImageAlpha(alpha);


    }

    public CubeRenderer getRenderer(){

        return surfaceView.getRenderer();

    }

    public void setModelToOpen(UserModel model){

        this.modelToRender = model;

    }

    public void openDialogBox(){

        EnterFigureNameDialog dialog = new EnterFigureNameDialog(EditorFragment.this.getActivity());
        dialog.setListener(new EnterFigureNameDialog.FigureNameDialogListener() {
            @Override
            public void enterFigureNamePressed(String name) {

                presenter.saveAsClicked(name);

            }
        });
        dialog.show();

    }
}
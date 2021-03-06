package com.testing.vladyslav.cubes.fragments;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.testing.vladyslav.cubes.ColorsLayoutCreator;
import com.testing.vladyslav.cubes.CubeRenderer;
import com.testing.vladyslav.cubes.CubeSurfaceView;
import com.testing.vladyslav.cubes.EditorColorsLayout;
import com.testing.vladyslav.cubes.PixioCube;
import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.database.entities.UserModel;
import com.testing.vladyslav.cubes.dialogs.AskToSaveDialog;
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
    private TextView txt_view_mode;
    private TextView debugTextView;

    private RelativeLayout toolsRow;
    private LinearLayoutCompat colorsRow;


    private EditorColorsLayout editor_color_row;

    //metrics
    private float floatColorWidth;
    private int intColorWidth;
    private int screenWidth;

    private float selectedColorWidth;
    private float nonSelectedColorWidth;

    //colors
    //scale size when select color
    float scaleSize = 1.4f;

    private float toolBarSize = 0.07f;
    private int colorsNumber = 16;
    private int nonTransparentAlpha = 255;
    private int transparentAlpha = 90;

    private CubeSurfaceView surfaceView;
    private ArrayList<ImageView> colorRow;

    private StudioActivityPresenter presenter;
    private UserModel modelToRender;
    private View view;
    private OnSurfaceViewCreatedListener surfaceViewCreatedListener;

    public interface OnSurfaceViewCreatedListener{
        void onSurfaceViewCreated();
    }

    private short[] colorOrder = new short[]{243, 244, 245, 246, 247, 248, 250, 249, 241, 242, 253, 252, 251, 254, 255, 240};

    public static final HashMap<Integer, Integer> colorCodeToImageName = new HashMap<Integer, Integer>(){
        {

            put(243, R.drawable.pixio_color_rectangle_16); //turquoise
            put(244, R.drawable.pixio_color_rectangle_15); //green
            put(245, R.drawable.pixio_color_rectangle_14); //light green
            put(246, R.drawable.pixio_color_rectangle_13); //yepixio_llow

            put(247, R.drawable.pixio_color_rectangle_12); //orange
            put(248, R.drawable.pixio_color_rectangle_11); //red
            put(250, R.drawable.pixio_color_rectangle_10); //pink
            put(249, R.drawable.pixio_color_rectangle_09); //vipixio_olet

            put(241, R.drawable.pixio_color_rectangle_08); //blue
            put(242, R.drawable.pixio_color_rectangle_07); //light blue
            put(253, R.drawable.pixio_color_rectangle_06); //brown
            put(252, R.drawable.pixio_color_rectangle_05); //light bpixio_rown

            put(251, R.drawable.pixio_color_rectangle_04); //tan
            put(254, R.drawable.pixio_color_rectangle_03); //white
            put(255, R.drawable.pixio_color_rectangle_02); //grey
            put(240, R.drawable.pixio_color_rectangle_01); //black
        }};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_editor, container, false);


        menu = view.findViewById(R.id.editorMenu);
        menu.setVisibility(View.INVISIBLE);
        img_menu_open = view.findViewById(R.id.ic_menu_open);
        img_menu_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.openMenuClicked();
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
        txt_view_mode = view.findViewById(R.id.txt_view_mode);

        txt_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.openClicked();
            }
        });
        txt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.saveClicked(null);
            }
        });
        txt_save_as.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenter.saveAsClicked(null);

            }
        });
        txt_view_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {presenter.viewModeClicked();
            }
        });

        toolsRow = view.findViewById(R.id.tools_row);
        colorsRow = view.findViewById(R.id.figure_colors_row);
        colorsRow.setVisibility(View.INVISIBLE);

        surfaceView = view.findViewById(R.id.surfaceView);
        if(modelToRender != null){
            surfaceView.getRenderer().setRenderingModel(modelToRender);
        }

        if(surfaceViewCreatedListener!= null){
            surfaceViewCreatedListener.onSurfaceViewCreated();
        }

        makeTransparentMiddleButtons();

        createColorRowLayout();

        surfaceView.getRenderer().setBuildingMode();
        img_add.setImageAlpha(nonTransparentAlpha);


        debugTextView = view.findViewById(R.id.debugTextView);
        surfaceView.setGesturesListener(new CubeSurfaceView.GesturesListener() {
            @Override
            public void log(String s) {
                debugTextView.setText(s);
            }
        });


        view.findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.backButtonPressed();
            }
        });


        return view;
    }

    public void setOnSurfaceViewCreatedListener(OnSurfaceViewCreatedListener listener){
        this.surfaceViewCreatedListener = listener;
    }

    public void setPresenter(StudioActivityPresenter presenter){this.presenter = presenter;}

    private void createColorRowLayout(){

        Display display = getActivity().getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        screenWidth = size. x;

        nonSelectedColorWidth = (float)screenWidth / (colorsNumber-1 + scaleSize);
        selectedColorWidth = scaleSize * nonSelectedColorWidth;

        editor_color_row = view.findViewById(R.id.editor_colors_row);
        editor_color_row.setActivity(this.getActivity());
        editor_color_row.setOnColorTouchListener(new EditorColorsLayout.OnColorTouchListener() {
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

        img_shadow_right.setTranslationY(selectedColorWidth - nonSelectedColorWidth);
        img_shadow_left.setTranslationY(selectedColorWidth - nonSelectedColorWidth);

        editor_color_row.addView(img_shadow_left);
        editor_color_row.addView(img_shadow_right);

        selectColor(0);
        setShadows(0);

        RelativeLayout.LayoutParams toolsRowParams = (RelativeLayout.LayoutParams) toolsRow.getLayoutParams();
        toolsRowParams.bottomMargin = Math.round(selectedColorWidth);
        toolsRow.setGravity(Gravity.BOTTOM);

        toolsRow.setLayoutParams(toolsRowParams);


    }

    private void selectColor(int colorPosition){

        surfaceView.getRenderer().setColor(colorOrder[colorPosition]);

        for (int i = 0; i< colorRow.size(); i++){

            ImageView image = colorRow.get(i);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) image.getLayoutParams();

            int marginLeft = 0;

            if(i < colorPosition){

                params.width = Math.round(nonSelectedColorWidth);
                params.height = Math.round(nonSelectedColorWidth);

                marginLeft = Math.round(i * nonSelectedColorWidth);

            }else if(i == colorPosition){

                if(colorPosition == colorsNumber -1){
                    params.width = Math.round(selectedColorWidth);
                    params.height = Math.round(selectedColorWidth);
                }else {
                    params.width = Math.round(selectedColorWidth) + 1;
                    params.height = Math.round(selectedColorWidth) + 1;
                }

                marginLeft = Math.round(i * nonSelectedColorWidth);

            }else{

                params.width = Math.round(nonSelectedColorWidth);
                params.height = Math.round(nonSelectedColorWidth);

                marginLeft = Math.round(i * nonSelectedColorWidth + (selectedColorWidth - nonSelectedColorWidth)) ;

            }

            params.leftMargin = marginLeft;
            params.bottomMargin = 0;

            image.setLayoutParams(params);

            if(i != colorPosition){
                image.setTranslationY(selectedColorWidth - nonSelectedColorWidth);
            }else{
                image.setTranslationY(0f);
            }


        }

    }

    private void setShadows (int selectedColorPosition){

        img_shadow_left.setVisibility(View.VISIBLE);
        img_shadow_right.setVisibility(View.VISIBLE);

        img_shadow_left.bringToFront();
        img_shadow_right.bringToFront();

        RelativeLayout.LayoutParams leftShadowParams = new RelativeLayout.LayoutParams(Math.round(nonSelectedColorWidth), Math.round(nonSelectedColorWidth));
        RelativeLayout.LayoutParams rightShadowParams = new RelativeLayout.LayoutParams(Math.round(nonSelectedColorWidth), Math.round(nonSelectedColorWidth));

        leftShadowParams.setMargins(Math.round(nonSelectedColorWidth * (selectedColorPosition-1)), 0, 0, 0);
        rightShadowParams.setMargins(Math.round(nonSelectedColorWidth * (selectedColorPosition) + Math.round(selectedColorWidth)), 0, 0, 0);

        img_shadow_right.setLayoutParams(rightShadowParams);
        img_shadow_left.setLayoutParams(leftShadowParams);

        if(selectedColorPosition == 0){
            img_shadow_left.setVisibility(View.INVISIBLE);
        }else if(selectedColorPosition == colorRow.size()) {
            img_shadow_right.setVisibility(View.INVISIBLE);
        }

        colorRow.get(selectedColorPosition).bringToFront();


    }

    private int viewToPosition(View v){

        return  colorRow.indexOf((ImageView)v);

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

        img_delete.setImageAlpha(transparentAlpha);
        img_add.setImageAlpha(transparentAlpha);
        img_change_color.setImageAlpha(transparentAlpha);


    }

    public void viewModeEnable(boolean b, ArrayList<PixioCube> cubes){

        if(b){

            closeMenu();
            ColorsLayoutCreator.createColorBlocksLayout(getActivity().getApplicationContext(), cubes, colorsRow, null);
            txt_view_mode.setText("Editor mode");
            Animation down = AnimationUtils.loadAnimation(getContext(),R.anim.editor_toolbar_slide_down);
            editor_color_row.startAnimation(down);
            toolsRow.startAnimation(down);
            toolsRow.setVisibility(View.INVISIBLE);
            editor_color_row.setVisibility(View.INVISIBLE);


            Animation up = AnimationUtils.loadAnimation(getContext(),R.anim.editor_toolbar_slide_up);
            colorsRow.startAnimation(up);
            colorsRow.setVisibility(View.VISIBLE);

        }else{
            closeMenu();
            txt_view_mode.setText("View mode");
            Animation up = AnimationUtils.loadAnimation(getContext(),R.anim.editor_toolbar_slide_up);
            toolsRow.startAnimation(up);
            editor_color_row.startAnimation(up);
            toolsRow.setVisibility(View.VISIBLE);
            editor_color_row.setVisibility(View.VISIBLE);


            Animation down = AnimationUtils.loadAnimation(getContext(),R.anim.editor_toolbar_slide_down);
            colorsRow.startAnimation(down);
            colorsRow.setVisibility(View.INVISIBLE);

        }

    }

    public CubeRenderer getRenderer(){

        return surfaceView.getRenderer();

    }


    public void setModelToOpen(UserModel model){

        this.modelToRender = model;

    }

    public void openEnterNameDialogBox(EnterFigureNameDialog.FigureNameDialogListener callback, String defText){

        EnterFigureNameDialog dialog = new EnterFigureNameDialog(EditorFragment.this.getActivity(), defText);
        dialog.setListener(callback);
        dialog.show();

    }

    public void openAskToSaveDialogBox(AskToSaveDialog.SaveChangesDialogListener callback){
        AskToSaveDialog dialog = new AskToSaveDialog(EditorFragment.this.getActivity());
        dialog.setListener(callback);
        dialog.show();
    }

    @Override
    public void setBackwardButtonVisible(Boolean visible) {
        if(!visible){
            img_cancel.setImageAlpha(transparentAlpha);
        }else{
            img_cancel.setImageAlpha(nonTransparentAlpha);
        }
    }

    @Override
    public void setForwardButtonVisible(Boolean visible) {
        if(!visible){
            img_repeat.setImageAlpha(transparentAlpha);
        }else{
            img_repeat.setImageAlpha(nonTransparentAlpha);
        }
    }




}

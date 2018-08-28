package com.testing.vladyslav.cubes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.testing.vladyslav.cubes.data.CubeDataHolder;

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


    private TextView txt_isTouched;

    private int graphicsQuality = 1;
    private int nonTransparentAlpha = 255;

    private CubeSurfaceView surfaceView;


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




        img_color_black = findViewById(R.id.black);
        img_color_black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(240);
            }
        });
        img_color_blue = findViewById(R.id.blue);
        img_color_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(241);
            }
        });
        img_color_brown = findViewById(R.id.brown);
        img_color_brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(253);
            }
        });
        img_color_green = findViewById(R.id.green);
        img_color_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(244);
            }
        });
        img_color_grey = findViewById(R.id.grey);
        img_color_grey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(255);
            }
        });
        img_color_orange = findViewById(R.id.orange);
        img_color_orange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(247);
            }
        });
        img_color_pink = findViewById(R.id.pink);
        img_color_pink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(250);
            }
        });
        img_color_light_blue = findViewById(R.id.light_blue);
        img_color_light_blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(242);
            }
        });
        img_color_light_brown = findViewById(R.id.light_brown);
        img_color_light_brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(252);
            }
        });
        img_color_light_green = findViewById(R.id.light_green);
        img_color_light_green.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(245);
            }
        });
        img_color_pirple = findViewById(R.id.pirple);
        img_color_pirple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(249);
            }
        });
        img_color_red = findViewById(R.id.red);
        img_color_red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(248);
            }
        });
        img_color_tan = findViewById(R.id.tan);
        img_color_tan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(251);
            }
        });
        img_color_turcouse = findViewById(R.id.turqouse);
        img_color_turcouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(243);
            }
        });
        img_color_white = findViewById(R.id.white);
        img_color_white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(254);
            }
        });
        img_color_yellow = findViewById(R.id.yellow);
        img_color_yellow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                surfaceView.getRenderer().setColor(246);
            }
        });


        txt_isTouched = findViewById(R.id.txt_isTouched);




        surfaceView.setListener(this);

        makeTransparentMiddleButtons();


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

package com.testing.vladyslav.cubes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.util.TextResourceReader;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CubeRenderer.CubeRendererListener{



    private TextView txt_isTouched;

    private int graphicsQuality = 1;


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

        txt_isTouched = findViewById(R.id.txt_isTouched);

        final CubeSurfaceView surfaceView = findViewById(R.id.surfaceView);



        surfaceView.setListener(this);




    }

    public void setTxt_isTouchedText(String txt){

        if(txt_isTouched != null && txt != null)
            txt_isTouched.setText(txt);

    }

    @Override
    public void onTouched(String txt) {
        setTxt_isTouchedText(txt);
    }
}

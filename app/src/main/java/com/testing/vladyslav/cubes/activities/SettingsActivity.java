package com.testing.vladyslav.cubes.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.Settings;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.util.TextResourceReader;

public class SettingsActivity extends AppCompatActivity {

    TextView txt_low;
    TextView txt_medium;
    TextView txt_high;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        CheckBox unlimitedGridBox;
        CheckBox dynamicShadows;
        CheckBox antialiasing;
        CheckBox debugTextView;
        CheckBox highLightGridCenterCheckBox;

        unlimitedGridBox = findViewById(R.id.unlimited_grid_checkBox);
        unlimitedGridBox.setChecked(Settings.unlimitedGrid);
        unlimitedGridBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.unlimitedGrid = b;
            }
        });


        dynamicShadows = findViewById(R.id.dynamic_shadows_checkBox);
        dynamicShadows.setChecked(Settings.dynamicShadows);
        dynamicShadows.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.dynamicShadows = b;
            }
        });

        antialiasing = findViewById(R.id.antialiasing_checkBox);
        antialiasing.setChecked(Settings.antialiasing);
        antialiasing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.antialiasing = b;
            }
        });

        highLightGridCenterCheckBox = findViewById(R.id.highlight_gridCenter_checkBox);
        highLightGridCenterCheckBox.setChecked(Settings.highLightedCentralLines);
        highLightGridCenterCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.highLightedCentralLines = b;
            }
        });

        debugTextView = findViewById(R.id.debug_textview_checkBox);
        debugTextView.setChecked(Settings.debugTextView);
        debugTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.debugTextView = b;
            }
        });

        txt_low = findViewById(R.id.txt_low);
        txt_medium = findViewById(R.id.txt_medium);
        txt_high = findViewById(R.id.txt_high);


        //check facetlist data loaded
        if(CubeDataHolder.getInstance().facetListHigh == null) {
            CubeDataHolder.getInstance().facetListLow = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_simple.obj");
            CubeDataHolder.getInstance().facetListMedium = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_medium.obj");
            CubeDataHolder.getInstance().facetListHigh = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_detailed.obj");
        }
        updateGraphicTextViews();

    }

    public void graphicQualityClicked(View v){

        if(v.getId() == R.id.txt_low){
            CubeDataHolder.getInstance().setGraphicsQuality(CubeDataHolder.QUALITY_LOW);
            Settings.graphicsQuality = Settings.LOW;
        }
        if(v.getId() == R.id.txt_medium){
            CubeDataHolder.getInstance().setGraphicsQuality(CubeDataHolder.QUALITY_MEDIUM);
            Settings.graphicsQuality = Settings.MEDIUM;
        }
        if(v.getId() == R.id.txt_high){
            CubeDataHolder.getInstance().setGraphicsQuality(CubeDataHolder.QUALITY_HIGH);
            Settings.graphicsQuality = Settings.ULTRA;
        }

        updateGraphicTextViews();

    }

    void updateGraphicTextViews(){

        txt_low.setTextColor(Color.BLACK);
        txt_medium.setTextColor(Color.BLACK);
        txt_high.setTextColor(Color.BLACK);

        if(Settings.graphicsQuality == Settings.LOW){
            txt_low.setTextColor(Color.GREEN);
        }
        if(Settings.graphicsQuality == Settings.MEDIUM){
            txt_medium.setTextColor(Color.GREEN);
        }
        if(Settings.graphicsQuality == Settings.ULTRA){
            txt_high.setTextColor(Color.GREEN);
        }

    }

}

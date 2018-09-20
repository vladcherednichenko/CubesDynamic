package com.testing.vladyslav.cubes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.data.CubeDataHolder;
import com.testing.vladyslav.cubes.util.TextResourceReader;

public class StartingActivity extends AppCompatActivity {

    private Button btnLow;
    private Button btnMedium;
    private Button btnHigh;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);



        if(CubeDataHolder.getInstance().facetListHigh == null) {
            CubeDataHolder.getInstance().facetListLow = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_simple.obj");
            CubeDataHolder.getInstance().facetListMedium = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_medium.obj");
            CubeDataHolder.getInstance().facetListHigh = TextResourceReader.getFacetsFromFileObject(getApplicationContext(), "cube_detailed.obj");
        }


        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartingActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnLow = findViewById(R.id.btnLow);
        btnLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                intent.putExtra("quality", 0);
                startActivity(intent);
            }
        });

        btnMedium = findViewById(R.id.btnMedium);
        btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                intent.putExtra("quality", 1);
                startActivity(intent);
            }
        });
        btnHigh = findViewById(R.id.btnHigh);
        btnHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                intent.putExtra("quality", 2);
                startActivity(intent);
            }
        });



    }
}

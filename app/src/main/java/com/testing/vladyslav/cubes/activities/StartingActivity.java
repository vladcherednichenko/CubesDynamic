package com.testing.vladyslav.cubes.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.testing.vladyslav.cubes.R;

public class StartingActivity extends AppCompatActivity {

    private Button btnStudio;
    private Button btnMedium;
    private Button btnHigh;
    private Button btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);




        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartingActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnStudio = findViewById(R.id.btn_studio);
        btnStudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), StudioActivity.class);
                startActivity(intent);
            }
        });




    }
}

package com.testing.vladyslav.cubes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.testing.vladyslav.cubes.R;
import com.testing.vladyslav.cubes.Settings;

public class SettingsActivity extends AppCompatActivity {

    private CheckBox unlimitedGridBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        unlimitedGridBox = findViewById(R.id.unlimited_grid_checkBox);
        unlimitedGridBox.setChecked(Settings.unlimitedGrid);
        unlimitedGridBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Settings.unlimitedGrid = b;
            }
        });

    }

}
